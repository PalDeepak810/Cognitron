package com.proc.proc.Service;

import com.proc.proc.Controller.dto.ActiveCrawlsResponse;
import com.proc.proc.Controller.dto.CrawlHistoryResponse;
import com.proc.proc.Controller.dto.QueueStatsResponse;
import com.proc.proc.Controller.dto.SystemMetricsResponse;
import com.proc.proc.Controller.dto.TraceResponse;
import com.proc.proc.Controller.dto.ThroughputResponse;
import com.proc.proc.Model.CrawlMetric;
import com.proc.proc.Model.CrawlStatus;
import com.proc.proc.Model.QueueSnapshot;
import com.proc.proc.Repository.CrawlMetricRepo;
import com.proc.proc.Repository.QueueSnapshotRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ObservatoryService {

    private final CrawlMetricRepo crawlMetricRepo;
    private final QueueSnapshotRepo queueSnapshotRepo;
    private final RabbitMQMonitorService rabbitMQMonitorService;

    public ObservatoryService(
            CrawlMetricRepo crawlMetricRepo,
            QueueSnapshotRepo queueSnapshotRepo,
            RabbitMQMonitorService rabbitMQMonitorService
    ) {
        this.crawlMetricRepo = crawlMetricRepo;
        this.queueSnapshotRepo = queueSnapshotRepo;
        this.rabbitMQMonitorService = rabbitMQMonitorService;
    }

    public ActiveCrawlsResponse getActiveCrawlsSnapshot() {
        List<ActiveCrawlsResponse.ActiveCrawlItem> activeItems = crawlMetricRepo
                .findTop20ByStatusOrderByStartedAtDesc(CrawlStatus.PROCESSING)
                .stream()
                .map(this::toActiveItem)
                .toList();

        List<ActiveCrawlsResponse.CompletedCrawlItem> completedItems = crawlMetricRepo
                .findTop20ByStatusOrderByCompletedAtDesc(CrawlStatus.COMPLETED)
                .stream()
                .map(this::toCompletedItem)
                .toList();

        return new ActiveCrawlsResponse(activeItems, completedItems);
    }

    private ActiveCrawlsResponse.ActiveCrawlItem toActiveItem(CrawlMetric metric) {
        long elapsedSeconds = 0L;
        if (metric.getStartedAt() != null) {
            elapsedSeconds = Math.max(0L, Duration.between(metric.getStartedAt(), LocalDateTime.now()).getSeconds());
        }

        return new ActiveCrawlsResponse.ActiveCrawlItem(
                metric.getId(),
                metric.getUrl(),
                metric.getStatus().name(),
                metric.getDepth(),
                metric.getStartedAt(),
                elapsedSeconds
        );
    }

    private ActiveCrawlsResponse.CompletedCrawlItem toCompletedItem(CrawlMetric metric) {
        return new ActiveCrawlsResponse.CompletedCrawlItem(
                metric.getId(),
                metric.getUrl(),
                metric.getStatus().name(),
                metric.getLinksDiscovered(),
                metric.getProcessingTimeMs(),
                metric.getCompletedAt()
        );
    }

    public QueueStatsResponse getQueueStatsSnapshot() {
        return new QueueStatsResponse(
                LocalDateTime.now(),
                rabbitMQMonitorService.getAllQueues()
        );
    }

    public SystemMetricsResponse getSystemMetricsSnapshot(String timeRange) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since = resolveSince(timeRange, now);

        long totalUrls = crawlMetricRepo.count();
        long newUrls = crawlMetricRepo.countByCreatedAtAfter(since);
        long completedLastWindow = crawlMetricRepo.countByStatusAndCreatedAtAfter(CrawlStatus.COMPLETED, since);
        long failedLastWindow = crawlMetricRepo.countByStatusAndCreatedAtAfter(CrawlStatus.FAILED, since);

        long completedAll = crawlMetricRepo.countByStatus(CrawlStatus.COMPLETED);
        long failedAll = crawlMetricRepo.countByStatus(CrawlStatus.FAILED);
        long processedAll = completedAll + failedAll;

        double successRate = processedAll == 0
                ? 0.0
                : (completedAll * 100.0) / processedAll;

        Double avgMsRaw = crawlMetricRepo.findAvgProcessingTimeMsByStatusSince(CrawlStatus.COMPLETED, since);
        if (avgMsRaw == null) {
            avgMsRaw = crawlMetricRepo.findAvgProcessingTimeMsByStatus(CrawlStatus.COMPLETED);
        }
        long avgProcessingTimeMs = avgMsRaw == null ? 0L : Math.max(0L, Math.round(avgMsRaw));

        List<CrawlMetricRepo.DomainCountView> topDomainViews = crawlMetricRepo.findTopDomainsSince(since, PageRequest.of(0, 5));
        long uniqueDomains = topDomainViews.stream()
                .map(CrawlMetricRepo.DomainCountView::getDomain)
                .filter(v -> v != null && !v.isBlank())
                .count();

        List<SystemMetricsResponse.TopDomainItem> topDomains = topDomainViews.stream()
                .map(view -> new SystemMetricsResponse.TopDomainItem(
                        normalizeDomain(view.getDomain()),
                        view.getCount(),
                        totalUrls == 0 ? 0.0 : roundOneDecimal((view.getCount() * 100.0) / totalUrls)
                ))
                .toList();

        return new SystemMetricsResponse(
                now,
                new SystemMetricsResponse.DatabaseMetrics(
                        totalUrls,
                        uniqueDomains,
                        new SystemMetricsResponse.LastWindowMetrics(newUrls, completedLastWindow, failedLastWindow)
                ),
                new SystemMetricsResponse.PerformanceMetrics(
                        roundOneDecimal(successRate),
                        avgProcessingTimeMs,
                        new SystemMetricsResponse.ErrorMetrics(failedAll)
                ),
                topDomains
        );
    }

    private LocalDateTime resolveSince(String timeRange, LocalDateTime now) {
        if (timeRange == null || timeRange.isBlank()) {
            return now.minusHours(24);
        }
        return switch (timeRange.trim().toLowerCase()) {
            case "1h" -> now.minusHours(1);
            case "7d" -> now.minusDays(7);
            case "24h" -> now.minusHours(24);
            default -> now.minusHours(24);
        };
    }

    private String normalizeDomain(String domain) {
        if (domain == null || domain.isBlank()) {
            return "unknown";
        }
        return domain;
    }

    private double roundOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    public ThroughputResponse getThroughputSnapshot(String timeRange, String interval) {
        LocalDateTime since = resolveSince(timeRange, LocalDateTime.now());
        String safeInterval = normalizeInterval(interval);
        long bucketSeconds = intervalSeconds(safeInterval);

        List<QueueSnapshot> snapshots = queueSnapshotRepo.findByRecordedAtAfterOrderByRecordedAtAsc(
                since,
                PageRequest.of(0, 500)
        );

        List<ThroughputResponse.DataPoint> dataPoints = bucketSnapshots(snapshots, bucketSeconds).stream()
                .sorted(Comparator.comparing(ThroughputResponse.DataPoint::timestamp).thenComparing(ThroughputResponse.DataPoint::queueName))
                .toList();

        return new ThroughputResponse(safeInterval, dataPoints);
    }

    private List<ThroughputResponse.DataPoint> bucketSnapshots(List<QueueSnapshot> snapshots, long bucketSeconds) {
        if (snapshots == null || snapshots.isEmpty()) {
            return List.of();
        }

        Map<String, BucketAccumulator> buckets = new HashMap<>();
        for (QueueSnapshot snapshot : snapshots) {
            if (snapshot.getRecordedAt() == null) {
                continue;
            }
            long epoch = snapshot.getRecordedAt().toEpochSecond(ZoneOffset.UTC);
            long bucketStartEpoch = (epoch / bucketSeconds) * bucketSeconds;
            String queueName = snapshot.getQueueName() == null ? "unknown" : snapshot.getQueueName();
            String key = queueName + "|" + bucketStartEpoch;
            BucketAccumulator acc = buckets.computeIfAbsent(key, k -> new BucketAccumulator(queueName, bucketStartEpoch));
            acc.add(snapshot);
        }

        return buckets.values().stream()
                .map(BucketAccumulator::toDataPoint)
                .toList();
    }

    private ThroughputResponse.DataPoint toThroughputPoint(QueueSnapshot snapshot) {
        return new ThroughputResponse.DataPoint(
                snapshot.getRecordedAt(),
                snapshot.getQueueName(),
                snapshot.getMessageCount(),
                snapshot.getConsumerCount(),
                snapshot.getMessagesPerSecond()
        );
    }

    private String normalizeInterval(String interval) {
        if (interval == null || interval.isBlank()) {
            return "5m";
        }
        String value = interval.trim().toLowerCase();
        return switch (value) {
            case "1m", "5m", "1h" -> value;
            default -> "5m";
        };
    }

    private long intervalSeconds(String interval) {
        return switch (interval) {
            case "1m" -> 60L;
            case "1h" -> 3600L;
            default -> 300L; // 5m default
        };
    }

    private static class BucketAccumulator {
        private final String queueName;
        private final long bucketStartEpoch;
        private long maxMessageCount = 0L;
        private double totalConsumers = 0.0;
        private double totalRate = 0.0;
        private int sampleCount = 0;

        private BucketAccumulator(String queueName, long bucketStartEpoch) {
            this.queueName = queueName;
            this.bucketStartEpoch = bucketStartEpoch;
        }

        private void add(QueueSnapshot snapshot) {
            long messages = snapshot.getMessageCount() != null ? snapshot.getMessageCount() : 0L;
            long consumers = snapshot.getConsumerCount() != null ? snapshot.getConsumerCount() : 0L;
            double rate = snapshot.getMessagesPerSecond() != null ? snapshot.getMessagesPerSecond() : 0.0;

            maxMessageCount = Math.max(maxMessageCount, messages);
            totalConsumers += consumers;
            totalRate += rate;
            sampleCount++;
        }

        private ThroughputResponse.DataPoint toDataPoint() {
            double avgConsumers = sampleCount == 0 ? 0.0 : totalConsumers / sampleCount;
            double avgRate = sampleCount == 0 ? 0.0 : totalRate / sampleCount;
            LocalDateTime ts = LocalDateTime.ofInstant(Instant.ofEpochSecond(bucketStartEpoch), ZoneOffset.UTC);
            return new ThroughputResponse.DataPoint(
                    ts,
                    queueName,
                    maxMessageCount,
                    Math.round(avgConsumers),
                    roundRate(avgRate)
            );
        }

        private Double roundRate(double value) {
            return Math.round(value * 100.0) / 100.0;
        }
    }

    public CrawlHistoryResponse getCrawlHistory(
            Integer page,
            Integer limit,
            String domain,
            String status,
            String fromDate,
            String toDate
    ) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeLimit = limit == null ? 50 : Math.max(1, Math.min(limit, 100));

        CrawlStatus crawlStatus = parseStatus(status).orElse(null);
        LocalDateTime from = parseDateTime(fromDate).orElse(null);
        LocalDateTime to = parseDateTime(toDate).orElse(null);
        String normalizedDomain = normalizeString(domain);

        var pageable = PageRequest.of(safePage - 1, safeLimit);
        List<CrawlMetric> rows = crawlMetricRepo.findHistory(normalizedDomain, crawlStatus, from, to, pageable);
        long total = crawlMetricRepo.countHistory(normalizedDomain, crawlStatus, from, to);

        List<CrawlHistoryResponse.Item> items = rows.stream()
                .map(this::toHistoryItem)
                .toList();

        return new CrawlHistoryResponse(total, safePage, safeLimit, items);
    }

    public TraceResponse getTrace(Long id) {
        Optional<CrawlMetric> target = crawlMetricRepo.findById(id);
        if (target.isEmpty()) {
            return new TraceResponse(null, List.of());
        }

        List<TraceResponse.TraceNode> path = new ArrayList<>();
        CrawlMetric current = target.get();

        // Follow parent URL chain via latest known metric row for each parent URL.
        while (current != null) {
            boolean isSeed = current.getParentUrl() == null || current.getParentUrl().isBlank();
            path.add(new TraceResponse.TraceNode(
                    current.getUrl(),
                    current.getDepth(),
                    current.getCreatedAt(),
                    current.getParentUrl(),
                    isSeed
            ));

            String parentUrl = current.getParentUrl();
            if (parentUrl == null || parentUrl.isBlank()) {
                break;
            }
            current = crawlMetricRepo.findTopByUrlOrderByCreatedAtDesc(parentUrl).orElse(null);
        }

        Collections.reverse(path);
        return new TraceResponse(target.get().getUrl(), path);
    }

    private CrawlHistoryResponse.Item toHistoryItem(CrawlMetric metric) {
        return new CrawlHistoryResponse.Item(
                metric.getId(),
                metric.getUrl(),
                metric.getDomain(),
                metric.getStatus() != null ? metric.getStatus().name() : "UNKNOWN",
                metric.getDepth(),
                metric.getLinksDiscovered(),
                metric.getProcessingTimeMs(),
                metric.getCreatedAt(),
                metric.getCompletedAt()
        );
    }

    private Optional<CrawlStatus> parseStatus(String status) {
        if (status == null || status.isBlank() || "all".equalsIgnoreCase(status)) {
            return Optional.empty();
        }
        try {
            return Optional.of(CrawlStatus.valueOf(status.trim().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private Optional<LocalDateTime> parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDateTime.parse(value));
        } catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }

    private String normalizeString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}

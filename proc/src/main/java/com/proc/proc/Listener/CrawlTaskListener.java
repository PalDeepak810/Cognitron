package com.proc.proc.Listener;

import com.proc.proc.Model.CrawlMetric;
import com.proc.proc.Model.CrawlStatus;
import com.proc.proc.Model.CrawlTask;
import com.proc.proc.Model.CrawledPage;
import com.proc.proc.Model.JobPosting;
import com.proc.proc.Model.VisitedUrl;
import com.proc.proc.Repository.CrawlMetricRepo;
import com.proc.proc.Repository.JobPostingRepo;
import com.proc.proc.Repository.VisitedUrlRepo;
import com.proc.proc.Service.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Component
public class CrawlTaskListener {

    private final CrawledPageService crawledPageService;
    private final LinkExtractorService linkExtractorService;
    private final DiscoveredLinkPublisher discoveredLinkPublisher;
    private final JobExtractionService jobExtractionService;
    private final SkillExtractor skillExtractor;
    private final JobPostingRepo jobPostingRepo;
    private final VisitedUrlRepo visitedUrlRepo;
    private final SeleniumService seleniumService;
    private final CrawlQuotaService crawlQuotaService;
    private final CrawlMetricRepo crawlMetricRepo;
    private final WebSocketBroadcastService webSocketBroadcastService;
    private final RateLimiterService rateLimiterService;

    public CrawlTaskListener(
            CrawledPageService crawledPageService,
            LinkExtractorService linkExtractorService,
            DiscoveredLinkPublisher discoveredLinkPublisher,
            JobExtractionService jobExtractionService,
            SkillExtractor skillExtractor,
            JobPostingRepo jobPostingRepo,
            VisitedUrlRepo visitedUrlRepo,
            SeleniumService seleniumService,
            CrawlQuotaService crawlQuotaService,
            CrawlMetricRepo crawlMetricRepo,
            WebSocketBroadcastService webSocketBroadcastService,
            RateLimiterService rateLimiterService) {
        this.crawledPageService = crawledPageService;
        this.linkExtractorService = linkExtractorService;
        this.discoveredLinkPublisher = discoveredLinkPublisher;
        this.jobExtractionService = jobExtractionService;
        this.skillExtractor = skillExtractor;
        this.jobPostingRepo = jobPostingRepo;
        this.visitedUrlRepo = visitedUrlRepo;
        this.seleniumService = seleniumService;
        this.crawlQuotaService = crawlQuotaService;
        this.crawlMetricRepo = crawlMetricRepo;
        this.webSocketBroadcastService = webSocketBroadcastService;
        this.rateLimiterService = rateLimiterService;
    }

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void processMessage(CrawlTask task) {
        if (task == null || task.getUrl() == null || task.getUrl().isBlank() || task.getConfig() == null) {
            return;
        }

        if (task.getDepth() > task.getConfig().getMaxDepth()) {
            return;
        }

        String hash = VisitedUrl.sha256(task.getUrl());
        if (visitedUrlRepo.existsByUrlHash(hash)) {
            return;
        }

        if (crawlQuotaService.remainingGlobalBudget() <= 0) {
            return;
        }

        if (!crawlQuotaService.tryAcquireRunSlot(task.getRunId(), task.getRunPageLimit())) {
            return;
        }

        visitedUrlRepo.save(new VisitedUrl(task.getUrl(), hash));

        CrawlMetric metric = new CrawlMetric();
        metric.setUrl(task.getUrl());
        metric.setDomain(extractDomain(task.getUrl()));
        metric.setDepth(task.getDepth());
        metric.setParentUrl(task.getParentUrl());
        metric.setStatus(CrawlStatus.PROCESSING);
        metric.setStartedAt(LocalDateTime.now());
        metric = crawlMetricRepo.save(metric);
        webSocketBroadcastService.broadcastCrawlStatus(metric);

        String domain = extractDomain(task.getUrl());
        rateLimiterService.acquire(domain);

        Document doc;
        try {
            if (seleniumService.isSeleniumRequired(task.getUrl())) {
                doc = seleniumService.fetchDynamicPage(task.getUrl());
                if (doc == null) {
                    markMetricFailed(metric, "Selenium fetch returned no document");
                    return;
                }
            } else {
                doc = Jsoup.connect(task.getUrl())
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(15000)
                        .get();
            }
        } catch (Exception e) {
            markMetricFailed(metric, e.getMessage());
            return;
        }

        String title = doc.title();
        String html = doc.html();
        String text = doc.body() != null ? doc.body().text() : "";

        if (!linkExtractorService.isRelevant(title, text, task.getConfig())) {
            saveCrawledPage(task, title, html, text, "COMPLETED", null);
            markMetricCompleted(metric, 0L);
            return;
        }

        try {
            JobPosting job = jobExtractionService.extractJobFromHtml(doc, task.getUrl());
            if (job != null && job.getTitle() != null && job.getCompany() != null && job.getLocation() != null) {
                if (job.getDescription() != null) {
                    String skills = skillExtractor.extractSkills(job.getDescription());
                    job.setSkills(skills);
                }

                Optional<JobPosting> existing = jobPostingRepo.findBySourceAndApplicationLink(
                        job.getSource(),
                        job.getApplicationLink()
                );

                if (existing.isEmpty()) {
                    jobPostingRepo.save(job);
                }
            }
        } catch (Exception ignored) {
        }

        saveCrawledPage(task, title, html, text, "COMPLETED", null);

        Set<String> links = linkExtractorService.extractLinks(html);
        Set<String> filteredLinks = linkExtractorService.filterLinks(
                links,
                task.getUrl(),
                task.getDepth(),
                task.getConfig()
        );

        long remainingRunBudget = crawlQuotaService.remainingRunBudget(task.getRunId(), task.getRunPageLimit());
        long remainingGlobalBudget = crawlQuotaService.remainingGlobalBudget();
        long remainingBudget = Math.min(remainingRunBudget, remainingGlobalBudget);

        long publishedChildrenCount = 0;
        for (String link : filteredLinks) {
            if (remainingBudget <= 0) {
                break;
            }

            CrawlTask child = new CrawlTask();
            child.setUrl(link);
            child.setDepth(task.getDepth() + 1);
            child.setParentUrl(task.getUrl());
            child.setConfig(task.getConfig());
            child.setRunId(task.getRunId());
            child.setRunPageLimit(task.getRunPageLimit());
            child.setRetryCount(0);

            discoveredLinkPublisher.publish(child);
            remainingBudget--;
            publishedChildrenCount++;
        }

        markMetricCompleted(metric, publishedChildrenCount);
    }

    private void saveCrawledPage(CrawlTask task, String title, String html, String text, String status, String errorMessage) {
        CrawledPage page = new CrawledPage();
        page.setUrl(task.getUrl());
        page.setDomain(extractDomain(task.getUrl()));
        page.setTitle(title);
        page.setHtml(html);
        page.setText(text);
        page.setDepth(task.getDepth());
        page.setRunId(task.getRunId());
        page.setParentUrl(task.getParentUrl());
        page.setStatus(status);
        page.setErrorMessage(errorMessage);
        page.setCrawledAt(LocalDateTime.now());
        crawledPageService.save(page);
    }

    private void markMetricCompleted(CrawlMetric metric, Long linksDiscovered) {
        LocalDateTime completedAt = LocalDateTime.now();
        metric.setStatus(CrawlStatus.COMPLETED);
        metric.setCompletedAt(completedAt);

        if (metric.getStartedAt() != null) {
            metric.setProcessingTimeMs(Duration.between(metric.getStartedAt(), completedAt).toMillis());
        }

        metric.setLinksDiscovered(linksDiscovered != null ? linksDiscovered : 0L);
        crawlMetricRepo.save(metric);
        webSocketBroadcastService.broadcastCrawlStatus(metric);
    }

    private void markMetricFailed(CrawlMetric metric, String errorMessage) {
        LocalDateTime completedAt = LocalDateTime.now();
        metric.setStatus(CrawlStatus.FAILED);
        metric.setCompletedAt(completedAt);

        if (metric.getStartedAt() != null) {
            metric.setProcessingTimeMs(Duration.between(metric.getStartedAt(), completedAt).toMillis());
        }

        metric.setErrorMessage(errorMessage);
        crawlMetricRepo.save(metric);
        webSocketBroadcastService.broadcastCrawlStatus(metric);
    }

    private String extractDomain(String url) {
        try {
            String host = URI.create(url).getHost();
            if (host == null || host.isBlank()) {
                return "unknown";
            }
            return host.startsWith("www.") ? host.substring(4) : host;
        } catch (Exception e) {
            return "unknown";
        }
    }
}

package com.proc.proc.Service;

import com.proc.proc.Controller.dto.DashboardOverviewResponse;
import com.proc.proc.Model.CrawlRunProgress;
import com.proc.proc.Repository.CrawlQuotaConsumptionRepo;
import com.proc.proc.Repository.CrawlRunProgressRepo;
import com.proc.proc.Repository.JobPostingRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {

    private final CrawlQuotaService crawlQuotaService;
    private final CrawlRunProgressRepo crawlRunProgressRepo;
    private final CrawlQuotaConsumptionRepo crawlQuotaConsumptionRepo;
    private final JobPostingRepo jobPostingRepo;

    public DashboardService(
            CrawlQuotaService crawlQuotaService,
            CrawlRunProgressRepo crawlRunProgressRepo,
            CrawlQuotaConsumptionRepo crawlQuotaConsumptionRepo,
            JobPostingRepo jobPostingRepo
    ) {
        this.crawlQuotaService = crawlQuotaService;
        this.crawlRunProgressRepo = crawlRunProgressRepo;
        this.crawlQuotaConsumptionRepo = crawlQuotaConsumptionRepo;
        this.jobPostingRepo = jobPostingRepo;
    }

    public DashboardOverviewResponse getOverview() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since24h = now.minusHours(24);

        long used24h = crawlQuotaService.usedGlobalBudgetLast24h();
        long remaining24h = crawlQuotaService.remainingGlobalBudget();
        int limit24h = crawlQuotaService.globalMaxPagesPer24h();
        LocalDateTime lastConsumedAt = crawlQuotaConsumptionRepo
                .findTopByOrderByConsumedAtDesc()
                .map(c -> c.getConsumedAt())
                .orElse(null);

        List<DashboardOverviewResponse.CrawlRunItem> latestRuns = crawlRunProgressRepo
                .findTop10ByOrderByUpdatedAtDesc()
                .stream()
                .map(this::toRunItem)
                .toList();

        long activeRunsLast10Minutes = crawlRunProgressRepo.countByUpdatedAtAfter(now.minusMinutes(10));

        long totalJobs = jobPostingRepo.count();
        long newJobsLast24h = jobPostingRepo.countByCreatedAtAfter(since24h);

        List<DashboardOverviewResponse.TitleTrend> topTitles = jobPostingRepo
                .findTopTitlesSince(since24h, PageRequest.of(0, 8))
                .stream()
                .map(v -> new DashboardOverviewResponse.TitleTrend(normalizeTitle(v.getTitle()), v.getCount()))
                .toList();

        List<DashboardOverviewResponse.SourceTrend> topSources = jobPostingRepo
                .findTopSourcesSince(since24h, PageRequest.of(0, 5))
                .stream()
                .map(v -> new DashboardOverviewResponse.SourceTrend(v.getSource(), v.getCount()))
                .toList();

        List<DashboardOverviewResponse.RecentJobItem> recentJobs = jobPostingRepo
                .findRecentJobSummaries(PageRequest.of(0, 12))
                .stream()
                .map(v -> new DashboardOverviewResponse.RecentJobItem(
                        v.getId(),
                        normalizeTitle(v.getTitle()),
                        v.getCompany(),
                        v.getLocation(),
                        v.getSource(),
                        v.getCreatedAt()
                ))
                .toList();

        return new DashboardOverviewResponse(
                now,
                new DashboardOverviewResponse.QuotaSnapshot(limit24h, used24h, remaining24h, lastConsumedAt),
                new DashboardOverviewResponse.CrawlSnapshot(activeRunsLast10Minutes, latestRuns),
                new DashboardOverviewResponse.JobSnapshot(totalJobs, newJobsLast24h, topTitles, topSources, recentJobs)
        );
    }

    private DashboardOverviewResponse.CrawlRunItem toRunItem(CrawlRunProgress run) {
        int runLimit = run.getRunLimit() != null ? run.getRunLimit() : 0;
        int processed = run.getProcessedCount() != null ? run.getProcessedCount() : 0;
        int remaining = Math.max(0, runLimit - processed);
        return new DashboardOverviewResponse.CrawlRunItem(
                run.getRunId(),
                processed,
                runLimit,
                remaining,
                run.getUpdatedAt()
        );
    }

    private String normalizeTitle(String title) {
        if (title == null || title.isBlank()) {
            return "Untitled role";
        }
        return title.trim();
    }
}

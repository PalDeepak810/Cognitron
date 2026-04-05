package com.proc.proc.Controller.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DashboardOverviewResponse(
        LocalDateTime generatedAt,
        QuotaSnapshot quota,
        CrawlSnapshot crawl,
        JobSnapshot jobs
) {
    public record QuotaSnapshot(
            int limit24h,
            long used24h,
            long remaining24h,
            LocalDateTime lastConsumedAt
    ) {
    }

    public record CrawlRunItem(
            String runId,
            int processedCount,
            int runLimit,
            int remaining,
            LocalDateTime updatedAt
    ) {
    }

    public record CrawlSnapshot(
            long activeRunsLast10Minutes,
            List<CrawlRunItem> latestRuns
    ) {
    }

    public record TitleTrend(
            String title,
            long count
    ) {
    }

    public record SourceTrend(
            String source,
            long count
    ) {
    }

    public record RecentJobItem(
            Long id,
            String title,
            String company,
            String location,
            String source,
            LocalDateTime createdAt
    ) {
    }

    public record JobSnapshot(
            long totalJobs,
            long newJobsLast24h,
            List<TitleTrend> topTitlesLast24h,
            List<SourceTrend> topSourcesLast24h,
            List<RecentJobItem> recentJobs
    ) {
    }
}

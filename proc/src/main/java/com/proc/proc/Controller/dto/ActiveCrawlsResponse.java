package com.proc.proc.Controller.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ActiveCrawlsResponse(
        List<ActiveCrawlItem> activeCrawls,
        List<CompletedCrawlItem> recentlyCompleted
) {
    public record ActiveCrawlItem(
            Long id,
            String url,
            String status,
            int depth,
            LocalDateTime startedAt,
            long elapsedSeconds
    ) {
    }

    public record CompletedCrawlItem(
            Long id,
            String url,
            String status,
            Long linksDiscovered,
            Long processingTimeMs,
            LocalDateTime completedAt
    ) {
    }
}

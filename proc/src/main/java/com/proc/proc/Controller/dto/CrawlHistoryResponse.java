package com.proc.proc.Controller.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CrawlHistoryResponse(
        long total,
        int page,
        int limit,
        List<Item> items
) {
    public record Item(
            Long id,
            String url,
            String domain,
            String status,
            int depth,
            Long linksDiscovered,
            Long processingTimeMs,
            LocalDateTime createdAt,
            LocalDateTime completedAt
    ) {
    }
}

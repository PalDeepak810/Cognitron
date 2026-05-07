package com.proc.proc.Controller.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SystemMetricsResponse(
        LocalDateTime generatedAt,
        DatabaseMetrics database,
        PerformanceMetrics performance,
        List<TopDomainItem> topDomains
) {
    public record DatabaseMetrics(
            long totalUrls,
            long uniqueDomains,
            LastWindowMetrics last24Hours
    ) {
    }

    public record LastWindowMetrics(
            long newUrls,
            long completedUrls,
            long failedUrls
    ) {
    }

    public record PerformanceMetrics(
            double successRate,
            long avgProcessingTimeMs,
            ErrorMetrics errors
    ) {
    }

    public record ErrorMetrics(
            long total
    ) {
    }

    public record TopDomainItem(
            String domain,
            long count,
            double percentage
    ) {
    }
}

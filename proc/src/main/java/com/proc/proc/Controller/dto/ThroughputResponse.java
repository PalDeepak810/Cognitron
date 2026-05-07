package com.proc.proc.Controller.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ThroughputResponse(
        String interval,
        List<DataPoint> dataPoints
) {
    public record DataPoint(
            LocalDateTime timestamp,
            String queueName,
            Long messageCount,
            Long consumerCount,
            Double messagesPerSecond
    ) {
    }
}

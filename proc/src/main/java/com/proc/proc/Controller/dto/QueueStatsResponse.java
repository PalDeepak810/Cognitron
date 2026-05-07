package com.proc.proc.Controller.dto;

import java.time.LocalDateTime;
import java.util.List;

public record QueueStatsResponse(
        LocalDateTime timestamp,
        List<QueueItem> queues
) {
    public record QueueItem(
            String name,
            long messageCount,
            long consumerCount,
            Double messagesPerSecond
    ) {
    }
}

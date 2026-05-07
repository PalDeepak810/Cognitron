package com.proc.proc.Service;

import com.proc.proc.Controller.dto.WebSocketMessage;
import com.proc.proc.Model.CrawlMetric;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class WebSocketBroadcastService {

    private static final String TOPIC = "/topic/observatory";

    private final SimpMessagingTemplate messagingTemplate;
    private final ObservatoryService observatoryService;

    public WebSocketBroadcastService(
            SimpMessagingTemplate messagingTemplate,
            ObservatoryService observatoryService
    ) {
        this.messagingTemplate = messagingTemplate;
        this.observatoryService = observatoryService;
    }

    @Scheduled(fixedRate = 2000)
    public void broadcastQueueStats() {
        var payload = observatoryService.getQueueStatsSnapshot();
        messagingTemplate.convertAndSend(
                TOPIC,
                new WebSocketMessage("QUEUE_UPDATE", payload, LocalDateTime.now())
        );
    }

    @Scheduled(fixedRate = 10000)
    public void broadcastThroughput() {
        var payload = observatoryService.getThroughputSnapshot("1h", "5m");
        messagingTemplate.convertAndSend(
                TOPIC,
                new WebSocketMessage("THROUGHPUT_UPDATE", payload, LocalDateTime.now())
        );
    }

    public void broadcastCrawlStatus(CrawlMetric metric) {
        if (metric == null) {
            return;
        }
        Map<String, Object> payload = Map.of(
                "id", metric.getId(),
                "url", metric.getUrl(),
                "status", metric.getStatus() != null ? metric.getStatus().name() : "UNKNOWN",
                "linksDiscovered", metric.getLinksDiscovered() != null ? metric.getLinksDiscovered() : 0L,
                "processingTimeMs", metric.getProcessingTimeMs() != null ? metric.getProcessingTimeMs() : 0L
        );
        messagingTemplate.convertAndSend(
                TOPIC,
                new WebSocketMessage("CRAWL_STATUS", payload, LocalDateTime.now())
        );
    }
}

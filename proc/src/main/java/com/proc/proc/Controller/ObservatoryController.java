package com.proc.proc.Controller;

import com.proc.proc.Controller.dto.ActiveCrawlsResponse;
import com.proc.proc.Controller.dto.CrawlHistoryResponse;
import com.proc.proc.Controller.dto.QueueStatsResponse;
import com.proc.proc.Controller.dto.SystemMetricsResponse;
import com.proc.proc.Controller.dto.TraceResponse;
import com.proc.proc.Controller.dto.ThroughputResponse;
import com.proc.proc.Service.ObservatoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/observatory")
public class ObservatoryController {

    private final ObservatoryService observatoryService;

    public ObservatoryController(ObservatoryService observatoryService) {
        this.observatoryService = observatoryService;
    }

    @GetMapping("/crawls/active")
    public ActiveCrawlsResponse getActiveCrawls() {
        return observatoryService.getActiveCrawlsSnapshot();
    }

    @GetMapping("/queues")
    public QueueStatsResponse getQueueStats() {
        return observatoryService.getQueueStatsSnapshot();
    }

    @GetMapping("/metrics")
    public SystemMetricsResponse getSystemMetrics(@RequestParam(required = false) String timeRange) {
        return observatoryService.getSystemMetricsSnapshot(timeRange);
    }

    @GetMapping("/throughput")
    public ThroughputResponse getThroughput(
            @RequestParam(required = false) String timeRange,
            @RequestParam(required = false) String interval
    ) {
        return observatoryService.getThroughputSnapshot(timeRange, interval);
    }

    @GetMapping("/crawls/history")
    public CrawlHistoryResponse getHistory(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) {
        return observatoryService.getCrawlHistory(page, limit, domain, status, fromDate, toDate);
    }

    @GetMapping("/trace/{id}")
    public TraceResponse getTrace(@PathVariable Long id) {
        return observatoryService.getTrace(id);
    }
}

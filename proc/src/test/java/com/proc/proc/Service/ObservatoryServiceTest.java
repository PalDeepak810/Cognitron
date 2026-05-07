package com.proc.proc.Service;

import com.proc.proc.Controller.dto.SystemMetricsResponse;
import com.proc.proc.Controller.dto.ThroughputResponse;
import com.proc.proc.Model.CrawlStatus;
import com.proc.proc.Repository.CrawlMetricRepo;
import com.proc.proc.Repository.QueueSnapshotRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

class ObservatoryServiceTest {

    private CrawlMetricRepo crawlMetricRepo;
    private QueueSnapshotRepo queueSnapshotRepo;
    private RabbitMQMonitorService rabbitMQMonitorService;
    private ObservatoryService observatoryService;

    @BeforeEach
    void setUp() {
        crawlMetricRepo = Mockito.mock(CrawlMetricRepo.class);
        queueSnapshotRepo = Mockito.mock(QueueSnapshotRepo.class);
        rabbitMQMonitorService = Mockito.mock(RabbitMQMonitorService.class);

        observatoryService = new ObservatoryService(crawlMetricRepo, queueSnapshotRepo, rabbitMQMonitorService);

        Mockito.when(crawlMetricRepo.findTop20ByStatusOrderByStartedAtDesc(any())).thenReturn(List.of());
        Mockito.when(crawlMetricRepo.findTop20ByStatusOrderByCompletedAtDesc(any())).thenReturn(List.of());
        Mockito.when(crawlMetricRepo.findTopDomainsSince(any(LocalDateTime.class), any())).thenReturn(List.of());
        Mockito.when(queueSnapshotRepo.findByRecordedAtAfterOrderByRecordedAtAsc(any(LocalDateTime.class), any())).thenReturn(List.of());
    }

    @Test
    void throughputDefaultsToFiveMinuteIntervalWhenMissing() {
        ThroughputResponse response = observatoryService.getThroughputSnapshot("1h", null);
        assertEquals("5m", response.interval());
        assertTrue(response.dataPoints().isEmpty());
    }

    @Test
    void throughputDefaultsToFiveMinuteIntervalWhenInvalid() {
        ThroughputResponse response = observatoryService.getThroughputSnapshot("1h", "13m");
        assertEquals("5m", response.interval());
        assertTrue(response.dataPoints().isEmpty());
    }

    @Test
    void metricsSuccessRateIsZeroWhenNoProcessedRows() {
        Mockito.when(crawlMetricRepo.count()).thenReturn(0L);
        Mockito.when(crawlMetricRepo.countByCreatedAtAfter(any(LocalDateTime.class))).thenReturn(0L);
        Mockito.when(crawlMetricRepo.countByStatusAndCreatedAtAfter(any(CrawlStatus.class), any(LocalDateTime.class))).thenReturn(0L);
        Mockito.when(crawlMetricRepo.countByStatus(CrawlStatus.COMPLETED)).thenReturn(0L);
        Mockito.when(crawlMetricRepo.countByStatus(CrawlStatus.FAILED)).thenReturn(0L);
        Mockito.when(crawlMetricRepo.findAvgProcessingTimeMsByStatusSince(any(CrawlStatus.class), any(LocalDateTime.class))).thenReturn(null);
        Mockito.when(crawlMetricRepo.findAvgProcessingTimeMsByStatus(any(CrawlStatus.class))).thenReturn(null);

        SystemMetricsResponse metrics = observatoryService.getSystemMetricsSnapshot("24h");
        assertEquals(0.0, metrics.performance().successRate());
        assertEquals(0L, metrics.performance().avgProcessingTimeMs());
    }
}

package com.proc.proc.Controller;

import com.proc.proc.Controller.dto.ActiveCrawlsResponse;
import com.proc.proc.Controller.dto.CrawlHistoryResponse;
import com.proc.proc.Controller.dto.QueueStatsResponse;
import com.proc.proc.Controller.dto.SystemMetricsResponse;
import com.proc.proc.Controller.dto.TraceResponse;
import com.proc.proc.Controller.dto.ThroughputResponse;
import com.proc.proc.Service.ObservatoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ObservatoryController.class)
class ObservatoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObservatoryService observatoryService;

    @Test
    void queuesEndpointReturnsShape() throws Exception {
        QueueStatsResponse response = new QueueStatsResponse(
                LocalDateTime.now(),
                List.of(new QueueStatsResponse.QueueItem("content-crawl-queue", 10L, 1L, 2.5))
        );
        Mockito.when(observatoryService.getQueueStatsSnapshot()).thenReturn(response);

        mockMvc.perform(get("/api/observatory/queues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queues").isArray())
                .andExpect(jsonPath("$.queues[0].name").value("content-crawl-queue"));
    }

    @Test
    void metricsEndpointReturnsShape() throws Exception {
        SystemMetricsResponse response = new SystemMetricsResponse(
                LocalDateTime.now(),
                new SystemMetricsResponse.DatabaseMetrics(
                        100L,
                        5L,
                        new SystemMetricsResponse.LastWindowMetrics(20L, 15L, 3L)
                ),
                new SystemMetricsResponse.PerformanceMetrics(
                        90.0,
                        1234L,
                        new SystemMetricsResponse.ErrorMetrics(3L)
                ),
                List.of(new SystemMetricsResponse.TopDomainItem("linkedin.com", 40L, 40.0))
        );
        Mockito.when(observatoryService.getSystemMetricsSnapshot(Mockito.any())).thenReturn(response);

        mockMvc.perform(get("/api/observatory/metrics").param("timeRange", "24h"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.database.totalUrls").value(100))
                .andExpect(jsonPath("$.performance.successRate").value(90.0));
    }

    @Test
    void throughputEndpointReturnsShape() throws Exception {
        ThroughputResponse response = new ThroughputResponse(
                "5m",
                List.of(new ThroughputResponse.DataPoint(LocalDateTime.now(), "discovered-links-queue", 30L, 1L, 3.2))
        );
        Mockito.when(observatoryService.getThroughputSnapshot(Mockito.any(), Mockito.any())).thenReturn(response);

        mockMvc.perform(get("/api/observatory/throughput").param("timeRange", "1h").param("interval", "5m"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interval").value("5m"))
                .andExpect(jsonPath("$.dataPoints").isArray());
    }

    @Test
    void activeCrawlsEndpointReturnsShape() throws Exception {
        ActiveCrawlsResponse response = new ActiveCrawlsResponse(
                List.of(new ActiveCrawlsResponse.ActiveCrawlItem(1L, "https://example.com", "PROCESSING", 1, LocalDateTime.now(), 3L)),
                List.of(new ActiveCrawlsResponse.CompletedCrawlItem(2L, "https://example.org", "COMPLETED", 5L, 500L, LocalDateTime.now()))
        );
        Mockito.when(observatoryService.getActiveCrawlsSnapshot()).thenReturn(response);

        mockMvc.perform(get("/api/observatory/crawls/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeCrawls").isArray())
                .andExpect(jsonPath("$.recentlyCompleted").isArray());
    }

    @Test
    void historyAndTraceEndpointsReturn200() throws Exception {
        Mockito.when(observatoryService.getCrawlHistory(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(new CrawlHistoryResponse(0L, 1, 50, List.of()));
        Mockito.when(observatoryService.getTrace(Mockito.anyLong()))
                .thenReturn(new TraceResponse(null, List.of()));

        mockMvc.perform(get("/api/observatory/crawls/history"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/observatory/trace/1"))
                .andExpect(status().isOk());
    }
}

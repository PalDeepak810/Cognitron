package com.proc.proc.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proc.proc.Controller.dto.QueueStatsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class RabbitMQMonitorService {

    private static final List<String> OBSERVED_QUEUES = List.of(
            "discovered-links-queue",
            "content-crawl-queue"
    );

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String managementUrl;
    private final String username;
    private final String password;

    public RabbitMQMonitorService(
            ObjectMapper objectMapper,
            @Value("${rabbitmq.management.url:http://localhost:15672}") String managementUrl,
            @Value("${rabbitmq.management.username:guest}") String username,
            @Value("${rabbitmq.management.password:guest}") String password
    ) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
        this.managementUrl = managementUrl;
        this.username = username;
        this.password = password;
    }

    public List<QueueStatsResponse.QueueItem> getAllQueues() {
        List<QueueStatsResponse.QueueItem> result = new ArrayList<>();
        for (String queueName : OBSERVED_QUEUES) {
            result.add(fetchQueue(queueName));
        }
        return result;
    }

    private QueueStatsResponse.QueueItem fetchQueue(String queueName) {
        try {
            String encodedVHost = URLEncoder.encode("/", StandardCharsets.UTF_8);
            String encodedQueue = URLEncoder.encode(queueName, StandardCharsets.UTF_8);
            String endpoint = String.format("%s/api/queues/%s/%s", trimTrailingSlash(managementUrl), encodedVHost, encodedQueue);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Authorization", basicAuth())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return new QueueStatsResponse.QueueItem(queueName, 0L, 0L, 0.0);
            }

            JsonNode root = objectMapper.readTree(response.body());
            long messages = root.path("messages").asLong(0L);
            long consumers = root.path("consumers").asLong(0L);

            // Prefer total rate from message_stats.rate; fallback to deliver/get detail rate.
            Double rate = null;
            JsonNode messageStats = root.path("message_stats");
            if (!messageStats.isMissingNode()) {
                if (!messageStats.path("rate").isMissingNode()) {
                    rate = messageStats.path("rate").asDouble(0.0);
                } else if (!messageStats.path("deliver_get_details").path("rate").isMissingNode()) {
                    rate = messageStats.path("deliver_get_details").path("rate").asDouble(0.0);
                }
            }
            if (rate == null) {
                rate = 0.0;
            }

            return new QueueStatsResponse.QueueItem(queueName, messages, consumers, rate);
        } catch (Exception e) {
            return new QueueStatsResponse.QueueItem(queueName, 0L, 0L, 0.0);
        }
    }

    private String basicAuth() {
        String raw = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "http://localhost:15672";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}

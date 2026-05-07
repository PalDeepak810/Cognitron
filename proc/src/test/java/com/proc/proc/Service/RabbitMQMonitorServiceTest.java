package com.proc.proc.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proc.proc.Controller.dto.QueueStatsResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RabbitMQMonitorServiceTest {

    @Test
    void getAllQueuesFallsBackSafelyWhenManagementApiUnavailable() {
        RabbitMQMonitorService service = new RabbitMQMonitorService(
                new ObjectMapper(),
                "http://127.0.0.1:1",
                "guest",
                "guest"
        );

        List<QueueStatsResponse.QueueItem> queues = service.getAllQueues();
        assertNotNull(queues);
        assertEquals(2, queues.size());
        assertTrue(queues.stream().allMatch(q -> q.messageCount() >= 0));
    }
}

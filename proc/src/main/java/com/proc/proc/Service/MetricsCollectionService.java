package com.proc.proc.Service;

import com.proc.proc.Controller.dto.QueueStatsResponse;
import com.proc.proc.Model.QueueSnapshot;
import com.proc.proc.Repository.QueueSnapshotRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MetricsCollectionService {

    private final RabbitMQMonitorService rabbitMQMonitorService;
    private final QueueSnapshotRepo queueSnapshotRepo;

    public MetricsCollectionService(
            RabbitMQMonitorService rabbitMQMonitorService,
            QueueSnapshotRepo queueSnapshotRepo
    ) {
        this.rabbitMQMonitorService = rabbitMQMonitorService;
        this.queueSnapshotRepo = queueSnapshotRepo;
    }

    @Scheduled(fixedRate = 30000)
    public void captureQueueSnapshots() {
        try {
            List<QueueStatsResponse.QueueItem> queueItems = rabbitMQMonitorService.getAllQueues();
            LocalDateTime recordedAt = LocalDateTime.now();

            for (QueueStatsResponse.QueueItem item : queueItems) {
                QueueSnapshot snapshot = new QueueSnapshot();
                snapshot.setQueueName(item.name());
                snapshot.setMessageCount(item.messageCount());
                snapshot.setConsumerCount(item.consumerCount());
                snapshot.setMessagesPerSecond(item.messagesPerSecond());
                snapshot.setRecordedAt(recordedAt);
                queueSnapshotRepo.save(snapshot);
            }
        } catch (Exception ignored) {
            // Keep scheduler resilient: one failed cycle must not break the app.
        }
    }
}

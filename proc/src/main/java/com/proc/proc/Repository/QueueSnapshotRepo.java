package com.proc.proc.Repository;

import com.proc.proc.Model.QueueSnapshot;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueSnapshotRepo extends JpaRepository<QueueSnapshot, Long> {
    List<QueueSnapshot> findTop100ByQueueNameOrderByRecordedAtDesc(String queueName);
    Optional<QueueSnapshot> findTopByQueueNameOrderByRecordedAtDesc(String queueName);
    List<QueueSnapshot> findByRecordedAtAfterOrderByRecordedAtAsc(LocalDateTime since, Pageable pageable);
}

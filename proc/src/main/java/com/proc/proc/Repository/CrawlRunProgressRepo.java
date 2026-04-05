package com.proc.proc.Repository;

import com.proc.proc.Model.CrawlRunProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CrawlRunProgressRepo extends JpaRepository<CrawlRunProgress, String> {
    List<CrawlRunProgress> findTop10ByOrderByUpdatedAtDesc();
    long countByUpdatedAtAfter(LocalDateTime since);
}

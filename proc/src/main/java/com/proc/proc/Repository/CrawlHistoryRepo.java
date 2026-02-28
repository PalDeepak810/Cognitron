package com.proc.proc.Repository;

import com.proc.proc.Model.CrawlHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CrawlHistoryRepo extends JpaRepository<CrawlHistory, Long> {
    
    Optional<CrawlHistory> findBySearchKey(String searchKey);
}

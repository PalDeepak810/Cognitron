package com.proc.proc.Repository;

import com.proc.proc.Model.CrawlQuotaConsumption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CrawlQuotaConsumptionRepo extends JpaRepository<CrawlQuotaConsumption, Long> {
    long countByConsumedAtAfter(LocalDateTime consumedAfter);
    Optional<CrawlQuotaConsumption> findTopByOrderByConsumedAtDesc();
}

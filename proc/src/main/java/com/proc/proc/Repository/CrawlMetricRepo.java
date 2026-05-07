package com.proc.proc.Repository;

import com.proc.proc.Model.CrawlMetric;
import com.proc.proc.Model.CrawlStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CrawlMetricRepo extends JpaRepository<CrawlMetric,Long> {
    List<CrawlMetric> findTop20ByStatusOrderByStartedAtDesc(CrawlStatus status);
    List<CrawlMetric> findTop20ByStatusOrderByCompletedAtDesc(CrawlStatus status);

    long countByStatus(CrawlStatus status);
    long countByCreatedAtAfter(LocalDateTime since);
    long countByStatusAndCreatedAtAfter(CrawlStatus status, LocalDateTime since);

    @Query("SELECT AVG(c.processingTimeMs) FROM CrawlMetric c WHERE c.status = :status")
    Double findAvgProcessingTimeMsByStatus(@Param("status") CrawlStatus status);

    @Query("SELECT AVG(c.processingTimeMs) FROM CrawlMetric c WHERE c.status = :status AND c.createdAt >= :since")
    Double findAvgProcessingTimeMsByStatusSince(@Param("status") CrawlStatus status, @Param("since") LocalDateTime since);

    @Query("""
            SELECT c.domain AS domain, COUNT(c) AS count
            FROM CrawlMetric c
            WHERE c.createdAt >= :since
            GROUP BY c.domain
            ORDER BY COUNT(c) DESC
            """)
    List<DomainCountView> findTopDomainsSince(@Param("since") LocalDateTime since, org.springframework.data.domain.Pageable pageable);

    interface DomainCountView {
        String getDomain();
        long getCount();
    }

    @Query("""
            SELECT c FROM CrawlMetric c
            WHERE (:domain IS NULL OR LOWER(c.domain) = LOWER(:domain))
              AND (:status IS NULL OR c.status = :status)
              AND (:fromDate IS NULL OR c.createdAt >= :fromDate)
              AND (:toDate IS NULL OR c.createdAt <= :toDate)
            ORDER BY c.createdAt DESC
            """)
    List<CrawlMetric> findHistory(
            @Param("domain") String domain,
            @Param("status") CrawlStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query("""
            SELECT COUNT(c) FROM CrawlMetric c
            WHERE (:domain IS NULL OR LOWER(c.domain) = LOWER(:domain))
              AND (:status IS NULL OR c.status = :status)
              AND (:fromDate IS NULL OR c.createdAt >= :fromDate)
              AND (:toDate IS NULL OR c.createdAt <= :toDate)
            """)
    long countHistory(
            @Param("domain") String domain,
            @Param("status") CrawlStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    Optional<CrawlMetric> findTopByUrlOrderByCreatedAtDesc(String url);
}

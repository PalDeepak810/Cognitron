package com.proc.proc.Repository;

import com.proc.proc.Model.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JobPostingRepo extends JpaRepository<JobPosting, Long> {

    interface TitleCountView {
        String getTitle();
        long getCount();
    }

    interface SourceCountView {
        String getSource();
        long getCount();
    }

    interface RecentJobView {
        Long getId();
        String getTitle();
        String getCompany();
        String getLocation();
        String getSource();
        LocalDateTime getCreatedAt();
    }
    
    List<JobPosting> findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(String title, String location);
    
    List<JobPosting> findBySkillsContainingIgnoreCase(String skills);

    long countByCreatedAtAfter(LocalDateTime since);
    
    @Query("SELECT j FROM JobPosting j ORDER BY j.postedDate DESC")
    List<JobPosting> findRecentJobs();

    @Query("SELECT j FROM JobPosting j WHERE j.postedDate >= :since ORDER BY j.postedDate DESC")
    List<JobPosting> findRecentJobsSince(@Param("since") LocalDateTime since);

    @Query("""
            SELECT j.title AS title, COUNT(j) AS count
            FROM JobPosting j
            WHERE j.createdAt >= :since
              AND j.title IS NOT NULL
              AND TRIM(j.title) <> ''
            GROUP BY j.title
            ORDER BY COUNT(j) DESC
            """)
    List<TitleCountView> findTopTitlesSince(@Param("since") LocalDateTime since, Pageable pageable);

    @Query("""
            SELECT j.source AS source, COUNT(j) AS count
            FROM JobPosting j
            WHERE j.createdAt >= :since
            GROUP BY j.source
            ORDER BY COUNT(j) DESC
            """)
    List<SourceCountView> findTopSourcesSince(@Param("since") LocalDateTime since, Pageable pageable);

    @Query("""
            SELECT
              j.id AS id,
              j.title AS title,
              j.company AS company,
              j.location AS location,
              j.source AS source,
              j.createdAt AS createdAt
            FROM JobPosting j
            ORDER BY j.createdAt DESC
            """)
    List<RecentJobView> findRecentJobSummaries(Pageable pageable);
    
    Optional<JobPosting> findBySourceAndApplicationLink(String source, String applicationLink);
    
    List<JobPosting> findByCompanyIgnoreCase(String company);
}

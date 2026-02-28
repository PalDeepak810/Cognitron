package com.proc.proc.Repository;

import com.proc.proc.Model.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobPostingRepo extends JpaRepository<JobPosting, Long> {
    
    List<JobPosting> findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(String title, String location);
    
    List<JobPosting> findBySkillsContainingIgnoreCase(String skills);
    
    @Query("SELECT j FROM JobPosting j ORDER BY j.postedDate DESC")
    List<JobPosting> findRecentJobs();
    
    Optional<JobPosting> findBySourceAndApplicationLink(String source, String applicationLink);
    
    List<JobPosting> findByCompanyIgnoreCase(String company);
}

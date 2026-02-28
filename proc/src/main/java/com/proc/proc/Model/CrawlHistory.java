package com.proc.proc.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crawl_history")
public class CrawlHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 500)
    private String searchKey;
    
    @Column(nullable = false)
    private LocalDateTime lastCrawledAt;
    
    @Column(nullable = false)
    private Integer jobsFound;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastCrawledAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public LocalDateTime getLastCrawledAt() {
        return lastCrawledAt;
    }

    public void setLastCrawledAt(LocalDateTime lastCrawledAt) {
        this.lastCrawledAt = lastCrawledAt;
    }

    public Integer getJobsFound() {
        return jobsFound;
    }

    public void setJobsFound(Integer jobsFound) {
        this.jobsFound = jobsFound;
    }
}

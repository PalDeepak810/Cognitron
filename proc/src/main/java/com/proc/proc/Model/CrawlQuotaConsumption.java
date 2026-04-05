package com.proc.proc.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "crawl_quota_consumption")
public class CrawlQuotaConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String runId;

    @Column(nullable = false)
    private LocalDateTime consumedAt;

    public CrawlQuotaConsumption() {
    }

    public CrawlQuotaConsumption(String runId) {
        this.runId = runId;
    }

    public Long getId() {
        return id;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public LocalDateTime getConsumedAt() {
        return consumedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (consumedAt == null) {
            consumedAt = LocalDateTime.now();
        }
    }
}

package com.proc.proc.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "crawled_page", indexes = {
    @Index(name = "idx_url", columnList = "url", unique = true),
    @Index(name = "idx_domain", columnList = "domain"),
    @Index(name = "idx_run_id", columnList = "runId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrawledPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String url;

    private String domain;

    private String title;

    @Lob
    @Column(columnDefinition = "text")
    private String html;

    @Lob
    @Column(columnDefinition = "text")
    private String text;

    private int depth;

    @CreationTimestamp
    private LocalDateTime crawledAt;

    private String runId;

    private String parentUrl;

    private String status;

    @Column(length = 1000)
    private String errorMessage;
}

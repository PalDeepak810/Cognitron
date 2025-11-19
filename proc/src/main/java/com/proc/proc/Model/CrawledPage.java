package com.proc.proc.Model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class CrawledPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String url;
    String title;
    @Column(columnDefinition = "LONGTEXT")
    String text;

    @Column(columnDefinition = "LONGTEXT")
    String html;
    String source;

    LocalDateTime crawledAt;

    public CrawledPage() {
    }

    public CrawledPage(Long id, String url, String title, String text, String html, String source, LocalDateTime crawledAt) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.text = text;
        this.html = html;
        this.source = source;
        this.crawledAt = crawledAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getCrawledAt() {
        return crawledAt;
    }

    public void setCrawledAt(LocalDateTime crawledAt) {
        this.crawledAt = crawledAt;
    }
}

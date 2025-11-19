package com.dp.crawl.Model;

import java.time.OffsetDateTime;

public class CrawlMessage {
    private String url;
    private String title;
    private String text;
    private String html;
    private String source;

    private OffsetDateTime crawledAt;

    public CrawlMessage() {
    }

    public CrawlMessage(String url, String title, String text, String html, String source, OffsetDateTime crawledAt) {
        this.url = url;
        this.title = title;
        this.text = text;
        this.html = html;
        this.source = source;
        this.crawledAt = crawledAt;
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

    public OffsetDateTime getCrawledAt() {
        return crawledAt;
    }

    public void setCrawledAt(OffsetDateTime crawledAt) {
        this.crawledAt = crawledAt;
    }
}

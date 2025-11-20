package com.proc.proc.Model;

import java.time.OffsetDateTime;

public class CrawlMessage {
    private String url;
    private String title;
    private String text;
    private String html;
    private String source;

    private OffsetDateTime crawledAt;

    private int depth;
    private String parentUrl;



    public CrawlMessage() {
    }

    public CrawlMessage(String url, String title, String text, String html, String source, OffsetDateTime crawledAt,int depth,String parenturl) {
        this.url = url;
        this.title = title;
        this.text = text;
        this.html = html;
        this.source = source;
        this.crawledAt = crawledAt;
        this.depth=depth;
        this.parentUrl=parenturl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
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

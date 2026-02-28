package com.dp.crawl.Model;

import java.util.List;

public class CrawlRequestBody {

    private String url;

    private List<String> topicKeywords;

    private Integer maxDepth;
    private Integer maxPages;
    private Boolean restrictDomain;

    public CrawlRequestBody() {
    }

    public CrawlRequestBody(String url, List<String> topicKeywords, Integer maxDepth, Integer maxPages, Boolean restrictDomain) {
        this.url = url;
        this.topicKeywords = topicKeywords;
        this.maxDepth = maxDepth;
        this.maxPages = maxPages;
        this.restrictDomain = restrictDomain;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getTopicKeywords() {
        return topicKeywords;
    }

    public void setTopicKeywords(List<String> topicKeywords) {
        this.topicKeywords = topicKeywords;
    }

    public Integer getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(Integer maxDepth) {
        this.maxDepth = maxDepth;
    }

    public Integer getMaxPages() {
        return maxPages;
    }

    public void setMaxPages(Integer maxPages) {
        this.maxPages = maxPages;
    }

    public Boolean getRestrictDomain() {
        return restrictDomain;
    }

    public void setRestrictDomain(Boolean restrictDomain) {
        this.restrictDomain = restrictDomain;
    }
}

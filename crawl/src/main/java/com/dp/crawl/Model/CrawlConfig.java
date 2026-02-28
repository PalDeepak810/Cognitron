package com.dp.crawl.Model;

import java.util.List;

public class CrawlConfig {
    private List<String> topicKeywords;
    private int maxDepth;

    private boolean restrictDomain;
    private int maxPages;

    public CrawlConfig() {
    }

    public CrawlConfig(List<String> topicKeywords, int maxDepth, int maxPages,boolean restrictDomain) {
        this.topicKeywords = topicKeywords;
        this.maxDepth = maxDepth;
        this.restrictDomain = restrictDomain;
        this.maxPages=maxPages;
    }

    public List<String> getTopicKeywords() {
        return topicKeywords;
    }

    public void setTopicKeywords(List<String> topicKeywords) {
        this.topicKeywords = topicKeywords;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public boolean isRestrictDomain() {
        return restrictDomain;
    }

    public void setRestrictDomain(boolean restrictDomain) {
        this.restrictDomain = restrictDomain;
    }

    public int getMaxPages() {
        return maxPages;
    }

    public void setMaxPages(int maxPages) {
        this.maxPages = maxPages;
    }
}

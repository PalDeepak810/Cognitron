package com.dp.crawl.Model;

public class CrawlRequestBody {

    private String url;

    public CrawlRequestBody() {}

    public CrawlRequestBody(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

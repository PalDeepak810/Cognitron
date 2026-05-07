package com.dp.crawl.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrawlTask {
    private String url;
    private int depth;
    private String runId;
    private String parentUrl;
    private Integer runPageLimit;
    private Integer retryCount;
    private CrawlConfig config;
}

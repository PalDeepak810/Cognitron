package com.dp.crawl.Service;

import com.dp.crawl.Model.CrawlConfig;
import com.dp.crawl.Model.CrawlRequestBody;
import com.dp.crawl.Model.CrawlTask;
import org.springframework.stereotype.Component;

@Component
public class CrawlTaskFactory {

    private static final int DEFAULT_MAX_DEPTH = 2;
    private static final int DEFAULT_MAX_PAGES = 10;
    private static final boolean DEFAULT_RESTRICT_DOMAIN = true;

    public CrawlConfig createManualConfig(CrawlRequestBody body) {
        CrawlConfig config = new CrawlConfig();
        config.setTopicKeywords(body.getTopicKeywords());
        config.setMaxDepth(body.getMaxDepth() != null ? body.getMaxDepth() : DEFAULT_MAX_DEPTH);
        config.setRestrictDomain(body.getRestrictDomain() != null ? body.getRestrictDomain() : DEFAULT_RESTRICT_DOMAIN);
        config.setMaxPages(body.getMaxPages() != null ? body.getMaxPages() : DEFAULT_MAX_PAGES);
        return config;
    }

    public CrawlConfig createJobSearchConfig() {
        CrawlConfig config = new CrawlConfig();
        config.setMaxDepth(1);
        config.setRestrictDomain(true);
        config.setMaxPages(5);
        return config;
    }

    public CrawlConfig createScheduledConfig(int maxDepth, int maxPages, boolean restrictDomain) {
        CrawlConfig config = new CrawlConfig();
        config.setMaxDepth(maxDepth);
        config.setRestrictDomain(restrictDomain);
        config.setMaxPages(maxPages);
        return config;
    }

    public CrawlTask createRootTask(String url, CrawlConfig config, String runId, Integer runPageLimit) {
        return CrawlTask.builder()
                .url(url)
                .depth(0)
                .runId(runId)
                .parentUrl(null)
                .runPageLimit(runPageLimit)
                .retryCount(0)
                .config(config)
                .build();
    }

    public CrawlTask createFromDiscovered(CrawlTask parentTask) {
        return CrawlTask.builder()
                .url(parentTask.getUrl())
                .depth(parentTask.getDepth())
                .runId(parentTask.getRunId())
                .parentUrl(parentTask.getParentUrl())
                .runPageLimit(parentTask.getRunPageLimit())
                .retryCount(parentTask.getRetryCount() == null ? 0 : parentTask.getRetryCount())
                .config(parentTask.getConfig())
                .build();
    }
}
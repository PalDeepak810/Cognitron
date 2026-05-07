package com.dp.crawl.Service;

import com.dp.crawl.Model.CrawlConfig;
import com.dp.crawl.Model.CrawlTask;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class CrawlScheduler {

    private final PublishService publishService;
    private final JobSiteRegistry jobSiteRegistry;
    private final CrawlTaskFactory crawlTaskFactory;

    private static final List<String> POPULAR_JOB_TITLES = Arrays.asList(
            "Software Engineer",
            "Data Scientist",
            "Full Stack Developer",
            "DevOps Engineer",
            "Product Manager"
    );

    private static final List<String> POPULAR_LOCATIONS = Arrays.asList(
            "Bangalore",
            "Mumbai",
            "Delhi",
            "Hyderabad",
            "Pune"
    );

    private static final int RUN_PAGE_LIMIT = 50;

    public CrawlScheduler(
            PublishService publishService,
            JobSiteRegistry jobSiteRegistry,
            CrawlTaskFactory crawlTaskFactory
    ) {
        this.publishService = publishService;
        this.jobSiteRegistry = jobSiteRegistry;
        this.crawlTaskFactory = crawlTaskFactory;
    }

    @Scheduled(fixedRate = 6 * 60 * 60 * 1000)
    public void scheduledJobCrawl() {
        System.out.println(">>> Scheduled job crawl started");

        int queuedCount = 0;
        String runId = UUID.randomUUID().toString();
        CrawlConfig config = crawlTaskFactory.createScheduledConfig(2, RUN_PAGE_LIMIT, true);

        for (String jobTitle : POPULAR_JOB_TITLES) {
            for (String location : POPULAR_LOCATIONS) {
                List<String> urls = jobSiteRegistry.buildSearchUrls(jobTitle, location);

                for (String url : urls) {
                    CrawlTask task = crawlTaskFactory.createRootTask(url, config, runId, RUN_PAGE_LIMIT);
                    publishService.publish(task);
                    queuedCount++;
                }
            }
        }

        System.out.println(">>> Scheduled crawl completed: " + queuedCount + " URLs queued");
    }
}
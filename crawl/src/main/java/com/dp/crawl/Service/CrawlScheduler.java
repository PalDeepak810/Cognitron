package com.dp.crawl.Service;

import com.dp.crawl.Model.CrawlConfig;
import com.dp.crawl.Model.CrawlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class CrawlScheduler {

    @Autowired
    private PublishService publishService;
    
    @Autowired
    private JobSiteRegistry jobSiteRegistry;
    
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
    
    @Scheduled(fixedRate = 6 * 60 * 60 * 1000) // Every 6 hours
    public void scheduledJobCrawl() {
        System.out.println(">>> Scheduled job crawl started");
        
        int queuedCount = 0;
        String runId = UUID.randomUUID().toString();
        
        for (String jobTitle : POPULAR_JOB_TITLES) {
            for (String location : POPULAR_LOCATIONS) {
                List<String> urls = jobSiteRegistry.buildSearchUrls(jobTitle, location);
                
                for (String url : urls) {
                    CrawlMessage msg = new CrawlMessage();
                    msg.setUrl(url);
                    msg.setDepth(0);
                    
                    CrawlConfig config = new CrawlConfig();
                    config.setMaxDepth(2); // Increased to allow following job links
                    config.setMaxPages(RUN_PAGE_LIMIT);
                    config.setRestrictDomain(true);
                    msg.setConfig(config);
                    msg.setRunId(runId);
                    msg.setRunPageLimit(RUN_PAGE_LIMIT);
                    
                    publishService.publish(msg);
                    queuedCount++;
                }
            }
        }
        
        System.out.println(">>> Scheduled crawl completed: " + queuedCount + " URLs queued");
    }
}

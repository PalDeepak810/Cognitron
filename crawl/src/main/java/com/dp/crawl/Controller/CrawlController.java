package com.dp.crawl.Controller;

import com.dp.crawl.Model.CrawlConfig;
import com.dp.crawl.Model.CrawlMessage;
import com.dp.crawl.Model.CrawlRequestBody;
import com.dp.crawl.Model.JobSearchRequest;
import com.dp.crawl.Service.JobSiteRegistry;
import com.dp.crawl.Service.PublishService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/crawl")
public class CrawlController {

    @Autowired
    private PublishService publishService;
    
    @Autowired
    private JobSiteRegistry jobSiteRegistry;

    @PostMapping
    public ResponseEntity<?> crawlUrl(@RequestBody CrawlRequestBody body) {

        if (body.getUrl() == null || body.getUrl().isBlank()) {
            return ResponseEntity.badRequest().body("URL is required");
        }

        CrawlConfig config = new CrawlConfig();
        config.setTopicKeywords(body.getTopicKeywords());
        config.setMaxDepth(body.getMaxDepth() != null ? body.getMaxDepth() : 2);
        config.setRestrictDomain(body.getRestrictDomain() != null ? body.getRestrictDomain() : true);
        config.setMaxPages(body.getMaxPages() != null ? body.getMaxPages() : 10);
        String runId = UUID.randomUUID().toString();

        CrawlMessage msg = new CrawlMessage();
        msg.setUrl(body.getUrl());
        msg.setDepth(0);
        msg.setConfig(config);
        msg.setRunId(runId);
        msg.setRunPageLimit(config.getMaxPages());

        publishService.publish(msg);

        return ResponseEntity.ok("Queued: " + msg.getUrl());
    }
    
    @PostMapping("/jobs/search")
    public ResponseEntity<?> searchJobs(@Valid @RequestBody JobSearchRequest request) {
        
        List<String> urls = jobSiteRegistry.buildSearchUrls(request.getJobTitle(), request.getLocation());
        
        CrawlConfig config = new CrawlConfig();
        config.setMaxDepth(1);
        config.setRestrictDomain(true);
        config.setMaxPages(5);
        String runId = UUID.randomUUID().toString();
        
        int queuedCount = 0;
        for (String url : urls) {
            CrawlMessage msg = new CrawlMessage();
            msg.setUrl(url);
            msg.setDepth(0);
            msg.setConfig(config);
            msg.setRunId(runId);
            msg.setRunPageLimit(config.getMaxPages());
            publishService.publish(msg);
            queuedCount++;
        }
        
        return ResponseEntity.ok(String.format("Queued %d job search URLs for '%s' in '%s'", 
                queuedCount, request.getJobTitle(), request.getLocation()));
    }
}

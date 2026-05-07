package com.dp.crawl.Controller;

import com.dp.crawl.Model.CrawlConfig;
import com.dp.crawl.Model.CrawlRequestBody;
import com.dp.crawl.Model.CrawlTask;
import com.dp.crawl.Model.JobSearchRequest;
import com.dp.crawl.Service.CrawlTaskFactory;
import com.dp.crawl.Service.JobSiteRegistry;
import com.dp.crawl.Service.PublishService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/crawl")
public class CrawlController {

    private final PublishService publishService;
    private final JobSiteRegistry jobSiteRegistry;
    private final CrawlTaskFactory crawlTaskFactory;

    public CrawlController(
            PublishService publishService,
            JobSiteRegistry jobSiteRegistry,
            CrawlTaskFactory crawlTaskFactory
    ) {
        this.publishService = publishService;
        this.jobSiteRegistry = jobSiteRegistry;
        this.crawlTaskFactory = crawlTaskFactory;
    }

    @PostMapping
    public ResponseEntity<String> crawlUrl(@RequestBody CrawlRequestBody body) {
        if (body.getUrl() == null || body.getUrl().isBlank()) {
            return ResponseEntity.badRequest().body("URL is required");
        }

        CrawlConfig config = crawlTaskFactory.createManualConfig(body);
        String runId = UUID.randomUUID().toString();
        CrawlTask task = crawlTaskFactory.createRootTask(body.getUrl(), config, runId, config.getMaxPages());

        publishService.publish(task);

        return ResponseEntity.accepted().body("Queued: " + task.getUrl());
    }

    @PostMapping("/jobs/search")
    public ResponseEntity<String> searchJobs(@Valid @RequestBody JobSearchRequest request) {
        List<String> urls = jobSiteRegistry.buildSearchUrls(request.getJobTitle(), request.getLocation());

        CrawlConfig config = crawlTaskFactory.createJobSearchConfig();
        String runId = UUID.randomUUID().toString();

        int queuedCount = 0;
        for (String url : urls) {
            CrawlTask task = crawlTaskFactory.createRootTask(url, config, runId, config.getMaxPages());
            publishService.publish(task);
            queuedCount++;
        }

        return ResponseEntity.accepted().body(
                String.format(
                        "Queued %d job search URLs for '%s' in '%s'",
                        queuedCount,
                        request.getJobTitle(),
                        request.getLocation()
                )
        );
    }
}
package com.dp.crawl.Controller;

import com.dp.crawl.Model.CrawlMessage;
import com.dp.crawl.Service.PublishService;
import com.dp.crawl.Service.WebFetcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dp.crawl.Model.CrawlRequestBody;

@RestController
@RequestMapping("/api/crawl")
public class CrawlController {
   @Autowired
   private WebFetcherService webFetcherService;

   @Autowired
   private PublishService publishService;

    @PostMapping
    public ResponseEntity<?> crawlUrl(@RequestBody CrawlRequestBody body) {
        if (body == null || body.getUrl() == null || body.getUrl().isBlank()) {
            return ResponseEntity.badRequest().body("Provide { \"url\": \"https://...\" }");
        }

        CrawlMessage msg = webFetcherService.fetch(body.getUrl());
        publishService.publish(msg);
        return ResponseEntity.ok("Queued: " + msg.getUrl());
    }
    
}

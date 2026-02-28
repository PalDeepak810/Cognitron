package com.dp.crawl.Service;

import com.dp.crawl.Model.CrawlConfig;
import com.dp.crawl.Model.CrawlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrawlScheduler {

    @Autowired
    private PublishService publishService;
    @Scheduled(fixedRate = 1000*60*60)
    public void scheduleCrawl(){
        String seedUrl="https://www.geeksforgeeks.org/";
        System.out.println("Crawling started for "+seedUrl);

        CrawlMessage m = new CrawlMessage();
        m.setUrl(seedUrl);
        m.setDepth(0);
        
        CrawlConfig config = new CrawlConfig();
        config.setTopicKeywords(List.of("java"));
        config.setMaxDepth(2);
        config.setMaxPages(10);
        config.setRestrictDomain(true);
        m.setConfig(config);

        publishService.publish(m);
    }
}

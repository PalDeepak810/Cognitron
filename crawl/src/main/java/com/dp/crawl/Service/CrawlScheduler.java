package com.dp.crawl.Service;

import com.dp.crawl.Model.CrawlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CrawlScheduler {

    @Autowired
    private PublishService publishService;
    @Scheduled(fixedRate = 1000*60*60)
    public void scheduleCrawl(){
        String seedUrl="https://www.geeksforgeeks.org/";
        System.out.println("Crawling started for"+seedUrl);

        CrawlMessage m = new CrawlMessage();
        m.setUrl(seedUrl);

        publishService.publish(m);
    }
}

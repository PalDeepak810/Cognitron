package com.proc.proc.Service;

import com.proc.proc.Model.CrawlMessage;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class DiscoveredLinkPublisher {

    @Autowired
    private AmqpTemplate amqpTemplate;

    private final String QUEUE_NAME = "content-crawl-queue";


    public void publish(String url) {
        CrawlMessage message = new CrawlMessage();
        message.setUrl(url);
        amqpTemplate.convertAndSend(QUEUE_NAME, message); // ✔ send CrawlMessage object
    }


    public void publishLinks(Set<String> links) {
        links.forEach(this::publish);
    }
}

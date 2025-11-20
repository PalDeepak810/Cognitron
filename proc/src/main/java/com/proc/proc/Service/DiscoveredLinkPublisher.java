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

    private final String QUEUE_NAME = "discovered-links-queue";


    public void publish(String url) {
        amqpTemplate.convertAndSend(QUEUE_NAME, url);
    }


    public void publishLinks(Set<String> links) {
        links.forEach(this::publish);
    }
}

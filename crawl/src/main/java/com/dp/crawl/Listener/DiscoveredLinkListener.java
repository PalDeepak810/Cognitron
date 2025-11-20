package com.dp.crawl.Listener;

import com.dp.crawl.Model.CrawlMessage;
import com.dp.crawl.Service.WebFetcherService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DiscoveredLinkListener {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Value("${cognitron.rabbitmq.queue}")
    private String crawlQueueName;

    @Autowired
    private WebFetcherService webFetcherService;

    @RabbitListener(queues = "${cognitron.rabbitmq.discovered-queue}")
    public void processDiscoveredLink(String url) {
        if (url == null || url.isBlank()) return;

        CrawlMessage message = webFetcherService.fetch(url);
        amqpTemplate.convertAndSend(crawlQueueName, message);
    }
}

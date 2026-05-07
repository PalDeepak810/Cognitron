package com.dp.crawl.Listener;

import com.dp.crawl.Model.CrawlTask;
import com.dp.crawl.Service.CrawlTaskFactory;
import com.dp.crawl.Service.PublishService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DiscoveredLinkListener {

    private final PublishService publishService;
    private final CrawlTaskFactory crawlTaskFactory;

    public DiscoveredLinkListener(PublishService publishService, CrawlTaskFactory crawlTaskFactory) {
        this.publishService = publishService;
        this.crawlTaskFactory = crawlTaskFactory;
    }

    @RabbitListener(queues = "${cognitron.rabbitmq.discovered-queue}")
    public void processDiscoveredLink(CrawlTask parentTask) {
        if (parentTask == null || parentTask.getUrl() == null || parentTask.getUrl().isBlank()) {
            return;
        }

        CrawlTask task = crawlTaskFactory.createFromDiscovered(parentTask);
        publishService.publish(task);
    }
}
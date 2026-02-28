package com.dp.crawl.Listener;

import com.dp.crawl.Model.CrawlMessage;
import com.dp.crawl.Service.PublishService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiscoveredLinkListener {

    @Autowired
    private PublishService publishService;

    @RabbitListener(queues = "${cognitron.rabbitmq.discovered-queue}")
    public void processDiscoveredLink(CrawlMessage parentMsg) {

        if (parentMsg == null || parentMsg.getUrl() == null || parentMsg.getConfig() == null) {
            return;
        }

        CrawlMessage msg = new CrawlMessage();
        msg.setUrl(parentMsg.getUrl());
        msg.setDepth(parentMsg.getDepth());   // depth already incremented by processor
        msg.setParentUrl(parentMsg.getParentUrl());
        msg.setConfig(parentMsg.getConfig()); // SAME CONFIG

        publishService.publish(msg);
    }
}

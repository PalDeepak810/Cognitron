package com.proc.proc.Service;

import com.proc.proc.Model.CrawlTask;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DiscoveredLinkPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String discoveredRoutingKey;

    public DiscoveredLinkPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange}") String exchange,
            @Value("${rabbitmq.discovered-routing-key:discovered.links}") String discoveredRoutingKey
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.discoveredRoutingKey = discoveredRoutingKey;
    }

    public void publish(CrawlTask task) {
        rabbitTemplate.convertAndSend(exchange, discoveredRoutingKey, task);
    }
}

package com.dp.crawl.Service;

import com.dp.crawl.Model.CrawlMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PublishService {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public PublishService(RabbitTemplate rabbitTemplate,
                          @Value("${cognitron.rabbitmq.exchange}") String exchange,
                          @Value("${cognitron.rabbitmq.routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publish(CrawlMessage message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}

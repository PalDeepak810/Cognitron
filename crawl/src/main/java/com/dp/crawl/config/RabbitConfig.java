package com.dp.crawl.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${cognitron.rabbitmq.queue}")
    private String queueName;

    @Value("${cognitron.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${cognitron.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${cognitron.rabbitmq.discovered-queue}")
    private String discoveredQueueName;

    @Bean
    public Queue crawlQueue() {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Queue discoveredQueue() {
        return QueueBuilder.durable(discoveredQueueName).build();
    }

    @Bean
    public TopicExchange crawlExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding crawlBinding() {
        return BindingBuilder
                .bind(crawlQueue())
                .to(crawlExchange())
                .with(routingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}

package com.proc.proc.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.queue}")
    private String crawlQueueName;

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    @Value("${rabbitmq.discovered-queue:discovered-links-queue}")
    private String discoveredQueueName;

    @Value("${rabbitmq.discovered-routing-key:discovered.links}")
    private String discoveredRoutingKey;

    @Bean
    public Queue crawlQueue() {
        return QueueBuilder.durable(crawlQueueName).build();
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
    public Binding discoveredBinding() {
        return BindingBuilder
                .bind(discoveredQueue())
                .to(crawlExchange())
                .with(discoveredRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setPrefetchCount(1);
        factory.setConcurrentConsumers(1);
        return factory;
    }
}
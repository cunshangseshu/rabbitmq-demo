package com.example.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTopologyConfig {
    // 正常业务 RabbitMQ 配置
    public static final String DEMO_EXCHANGE = "demo.hello.exchange";
    public static final String DEMO_QUEUE = "demo.hello.queue";
    public static final String DEMO_ROUTING_KEY = "demo.hello";

    // Retry RabbitMQ配置
    public static final String RETRY_EXCHANGE = "demo.retry.exchange";
    public static final String RETRY_QUEUE = "demo.retry.queue";
    public static final String RETRY_ROUTING_KEY = "demo.retry";

    // TTL RabbitMQ 配置
    public static final String TTL_EXCHANGE = "demo.ttl.exchange";
    public static final String TTL_QUEUE = "demo.ttl.queue";
    public static final String TTL_ROUTING_KEY = "demo.ttl";

    // Dead RabbitMQ 配置
    public static final String DEAD_EXCHANGE = "demo.dlx.exchange";
    public static final String DEAD_QUEUE = "demo.dlq.queue";
    public static final String DEAD_ROUTING_KEY = "demo.dead";

    @Bean
    public DirectExchange demoExchange() {
        return new DirectExchange(DEMO_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange retryExchange() {
        return new DirectExchange(RETRY_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange ttlExchange() {
        return new DirectExchange(TTL_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange(DEAD_EXCHANGE, true, false);
    }


    @Bean
    public Binding demoBinding(DirectExchange demoExchange, Queue demoQueue) {
        return BindingBuilder.
                bind(demoQueue).
                to(demoExchange).
                with(DEMO_ROUTING_KEY);
    }

    @Bean
    public Binding retryBinding() {
        return BindingBuilder.
                bind(retryQueue()).
                to(retryExchange()).
                with(RETRY_ROUTING_KEY);
    }

    @Bean
    public Binding ttlBinding() {
        return BindingBuilder.
                bind(ttlQueue()).
                to(ttlExchange()).
                with(TTL_ROUTING_KEY);
    }

    @Bean
    public Binding deadBinding() {
        return BindingBuilder.
                bind(deadQueue()).
                to(deadExchange()).
                with(DEAD_ROUTING_KEY);
    }

    @Bean
    public Queue demoQueue() {
        return QueueBuilder.
                durable(DEMO_QUEUE).
                deadLetterExchange(RETRY_EXCHANGE).
                deadLetterRoutingKey(RETRY_ROUTING_KEY).
                build();
    }

    @Bean
    public Queue retryQueue() {
        return QueueBuilder.
                durable(RETRY_QUEUE).
                ttl(5000).
                deadLetterExchange(DEMO_EXCHANGE).
                deadLetterRoutingKey(DEMO_ROUTING_KEY).
                build();
    }

    @Bean
    public Queue ttlQueue() {
        return QueueBuilder.
                durable(TTL_QUEUE).
                ttl(10000).
                deadLetterExchange(DEAD_EXCHANGE).
                deadLetterRoutingKey(DEAD_ROUTING_KEY).
                build();
    }

    @Bean
    public Queue deadQueue() {
        return QueueBuilder.durable(DEAD_QUEUE).build();
    }
}

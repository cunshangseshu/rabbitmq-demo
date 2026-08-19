package com.example.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTopologyConfig {
    /*
     * ==============================
     * 正常业务 RabbitMQ 配置
     * ==============================
     */
    public static final String DEMO_EXCHANGE = "demo.direct.exchange";
    public static final String DEMO_QUEUE = "demo.hello.queue";
    public static final String DEMO_ROUTING_KEY = "demo.hello";


    /*
     * ==============================
     * 死信 RabbitMQ 配置
     * ==============================
     */
    public static final String DEAD_EXCHANGE = "demo.dlx.exchange";
    public static final String DEAD_QUEUE = "demo.dlq.queue";
    public static final String DEAD_ROUTING_KEY = "demo.dead";


    /*
     * ==============================
     * TTL RabbitMQ 配置
     * ==============================
     */
    public static final String TTL_EXCHANGE = "demo.ttl.exchange";
    public static final String TTL_QUEUE = "demo.ttl.queue";
    public static final String TTL_ROUTING_KEY = "demo.ttl";

    /**
     * 创建正常 Exchange
     */
    @Bean
    public DirectExchange demoExchange() {
        return new DirectExchange(DEMO_EXCHANGE, true, false);
    }

    /**
     * 声明 Queue。
     * durable(true)：
     * RabbitMQ 重启后，Queue 仍然存在。
     * 注意：
     * Queue 持久化不等于消息一定持久化。
     * 后续还要给消息设置 persistent。
     */
    /*@Bean
    public Queue demoQueue() {
        return QueueBuilder.durable(DEMO_QUEUE).build();
    }*/

    @Bean
    public Binding demoBinding(DirectExchange demoExchange, Queue demoQueue) {
        return BindingBuilder.bind(demoQueue).to(demoExchange).with(DEMO_ROUTING_KEY);
    }

    @Bean
    public Queue demoQueue() {
        // 如果 DEMO_QUEUE 中的消息变成死信，就把消息发送给：demo.dlx.exchange 并使用 Routing Key：demo.dead；
        return QueueBuilder.durable(DEMO_QUEUE)
                .deadLetterExchange(DEAD_EXCHANGE)
                .deadLetterRoutingKey(DEAD_ROUTING_KEY)
                .build();
    }

    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange(DEAD_EXCHANGE, true, false);
    }

    @Bean
    public Queue deadQueue() {
        //新增一个消息队列里的消息统一过期时间；
        //这里得吐槽一下，spring这个方法写的很鸡肋，居然没有时间单位的！
        return QueueBuilder.durable(DEAD_QUEUE).build();
        //不指定TTL的写法；
        //return QueueBuilder.durable(DEAD_QUEUE).build();
    }

    @Bean
    public Binding deadBinding() {
        return BindingBuilder.bind(deadQueue()).to(deadExchange()).with(DEAD_ROUTING_KEY);
    }

    @Bean
    public Queue ttlQueue() {
        return QueueBuilder
                .durable(TTL_QUEUE)
                .ttl(5000)
                .deadLetterExchange(DEAD_EXCHANGE)
                .deadLetterRoutingKey(DEAD_ROUTING_KEY)
                .build();
    }

    /**
     * 创建TTL的Exchange
     */
    @Bean
    public DirectExchange ttlExchange() {
        return new DirectExchange(TTL_EXCHANGE, true, false);
    }

    /**
     * 创建TTL的Binding
     *
     */
    @Bean
    public Binding ttlBinding() {
        return BindingBuilder.bind(ttlQueue()).to(ttlExchange()).with(TTL_ROUTING_KEY);
    }
}

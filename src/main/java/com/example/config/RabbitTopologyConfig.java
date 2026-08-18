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
    /**
     * 交换机名称。
     * Exchange 负责接收 Producer 发来的消息，
     * 然后根据 Routing Key 和 Binding 进行路由。
     */
    public static final String DEMO_EXCHANGE = "demo.direct.exchange";
    /**
     * 队列名称。
     * Queue 用于保存等待 Consumer 处理的消息。
     */
    public static final String DEMO_QUEUE = "demo.hello.queue";
    /**
     * 路由键。
     * DirectExchange 要求消息的 Routing Key与 Binding Key 完全一致。
     */
    public static final String DEMO_ROUTING_KEY = "demo.hello";

    /*
     * ==============================
     * 死信 RabbitMQ 配置
     * ==============================
     */
    // 死信交换机
    public static final String DEAD_EXCHANGE = "demo.dlx.exchange";
    // 死信队列
    public static final String DEAD_QUEUE = "demo.dlq.queue";
    // 死信Routing key
    public static final String DEAD_ROUTING_KEY = "demo.dead";

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

    /**
     * 把 Queue 绑定到 Exchange。
     * 只有 Routing Key 为 demo.hello 的消息，才会从 Exchange 进入 demo.hello.queue。
     */
    @Bean
    public Binding demoBinding(DirectExchange demoExchange, Queue demoQueue) {
        return BindingBuilder.bind(demoQueue).to(demoExchange).with(DEMO_ROUTING_KEY);
    }

    /**
     * ==============================
     * 创建正常业务 Queue
     * ==============================
     * <p>
     * 这里是死信最关键的地方。
     */
    @Bean
    public Queue demoQueue() {
        // 如果 DEMO_QUEUE 中的消息变成死信，就把消息发送给：demo.dlx.exchange 并使用 Routing Key：demo.dead；
        return QueueBuilder.durable(DEMO_QUEUE)
                .deadLetterExchange(DEAD_EXCHANGE)
                .deadLetterRoutingKey(DEAD_ROUTING_KEY)
                .build();
    }

    /**
     * 创建死信 Exchange
     * <p>
     * 注意：DLX 本质就是普通 Exchange,我们这里继续使用 DirectExchange。
     */
    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange(DEAD_EXCHANGE, true, false);
    }

    /**
     * 创建死信 Queue
     */
    @Bean
    public Queue deadQueue() {
        return QueueBuilder.durable(DEAD_QUEUE).build();
    }

    /**
     * 创建死信 Binding
     */
    @Bean
    public Binding deadBinding() {
        return BindingBuilder.bind(deadQueue()).to(deadExchange()).with(DEAD_ROUTING_KEY);
    }
}

package com.example.config;

下 import org.springframework.amqp.core.*;
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

    /**
     * 声明 DirectExchange。
     * durable(true)：
     * RabbitMQ 重启后，Exchange 仍然存在。
     */
    @Bean
    public DirectExchange demoExchange() {
        return ExchangeBuilder.directExchange(DEMO_EXCHANGE).durable(true).build();
    }

    /**
     * 声明 Queue。
     * durable(true)：
     * RabbitMQ 重启后，Queue 仍然存在。
     * 注意：
     * Queue 持久化不等于消息一定持久化。
     * 后续还要给消息设置 persistent。
     */
    @Bean
    public Queue demoQueue() {
        return QueueBuilder.durable(DEMO_QUEUE).build();
    }

    /**
     * 把 Queue 绑定到 Exchange。
     * 只有 Routing Key 为 demo.hello 的消息，才会从 Exchange 进入 demo.hello.queue。
     */
    @Bean
    public Binding demoBinding(DirectExchange demoExchange, Queue demoQueue) {

        return BindingBuilder.bind(demoQueue).to(demoExchange).with(DEMO_ROUTING_KEY);
    }


    /*
     * ==============================
     * 死信 RabbitMQ 配置
     * ==============================
     */
    /**
     * 死信交换机
     */
    public static final String DEAD_EXCHAGE = "demo.dlx.exchage";
    /**
     * 死信队列
     */
    public static final String DEAD_QUEUE = "demo.dlq.exchange";
    /**
     * 死信Rounting key
     *
     */
    public static final String DEAD_ROUTING_KEY = "demo.dead";
    /**
     * ==============================
     * 2. 创建死信 Exchange
     * ==============================
     *
     * 注意：
     *
     * DLX 本质就是普通 Exchange。
     *
     * 我们这里继续使用 DirectExchange。
     */


}

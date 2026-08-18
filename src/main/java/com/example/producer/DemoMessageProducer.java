package com.example.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * [2026-8-15 12:17]
 * RabbitMQ 消息生产者
 * 这个类的职责非常单纯：
 * 1. 接收业务层传过来的消息;
 * 2. 指定 Exchange;
 * 3. 指定 Routing Key;
 * 4. 调用 RabbitTemplate 把消息发送到 RabbitMQ;
 * 当前完整链路：
 * Controller
 * ↓
 * DemoMessageProducer
 * ↓
 * RabbitTemplate
 * ↓
 * demo.direct.exchange
 * ↓
 * Routing Key = demo.hello
 * ↓
 * demo.hello.queue
 * 注意：
 * 当前我们故意还没有创建 Consumer,所以消息进入 Queue 后会一直处于 Ready 状态。
 */

@Component
public class DemoMessageProducer {
    /**
     * RabbitMQ 交换机名称。
     * 必须和我们之前创建的 Exchange 名字完全一致。
     */
    private static final String EXCHANGE_NAME = "demo.direct.exchange";

    /**
     * Routing Key。
     * Exchange 会根据这个 Routing Key,查找对应的 Binding，然后把消息路由到 Queue。
     */
    private static final String ROUTING_KEY = "demo.hello";

    /**
     * RabbitTemplate 是 Spring AMQP 提供的 RabbitMQ 操作工具。
     * 可以把它理解成：
     * JDBC 里面有 JdbcTemplate、
     * Redis 里面有 RedisTemplate、
     * RabbitMQ 里面有 RabbitTemplate，
     * 所以我们不需要自己创建 TCP 连接、AMQP Channel、序列化、发送协议等。
     * ❤❤❤总结Spring Boot已经帮我们配置好了。
     */
    private final RabbitTemplate rabbitTemplate;

    /**
     * 构造器注入 RabbitTemplate。
     * Spring Boot 启动时：
     * 1. 根据 application.yml 创建 RabbitMQ ConnectionFactory;
     * 2. 创建 RabbitTemplate;
     * 3. 再把 RabbitTemplate 注入这个类;
     * 使用构造器注入也是 Spring Boot 项目中推荐的方式。
     */
    public DemoMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送消息。
     *
     * @param message 要发送到 RabbitMQ 的消息内容
     */
    public void sendMessage(String message) {
        /*
          convertAndSend(exchange,routingKey,message)
               参数1：Exchange 名字;
               参数2：Routing Key;
               参数3：真正发送的数据;
          RabbitMQ 收到之后：
               demo.direct.exchange
                       ↓
               查找 Routing Key = demo.hello 的 Binding
                       ↓
               demo.hello.queue
          */
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);

        /*
         * 这里打印日志只是为了方便我们学习阶段观察。
         */
        System.out.println("Producer已发送消息：" + message);
    }
}

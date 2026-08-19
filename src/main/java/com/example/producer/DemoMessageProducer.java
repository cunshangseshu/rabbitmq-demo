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
    private static final String EXCHANGE_NAME = "demo.direct.exchange";
    private static final String ROUTING_KEY = "demo.hello";
    private static final String TTL_EXCHANGE_NAME = "demo.ttl.exchange";
    private static final String TTL_ROUTING_KEY = "demo.ttl";
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
     * @param message 要发送到 RabbitMQ 的消息内容
     */
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);
        //这里打印日志只是为了方便我们学习阶段观察。
        System.out.println("Producer已发送消息：" + message);
    }

    /**
     * 发送消息给 TTL RabbitMQ。
     *
     * @param message 要发送到 RabbitMQ 的消息内容
     *
     */
    public void sendTtlMessage(String message) {
        rabbitTemplate.convertAndSend(TTL_EXCHANGE_NAME, TTL_ROUTING_KEY, message);
        System.out.println("Producer已发送给TTL RabbitMQ的消息：" + message);
    }
}

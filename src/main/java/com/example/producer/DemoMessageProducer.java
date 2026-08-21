package com.example.producer;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
public class DemoMessageProducer {
    // 正常业务 RabbitMQ 配置
    private static final String DEMO_EXCHANGE_NAME = "demo.hello.exchange";
    private static final String DEMO_ROUTING_KEY = "demo.hello";
    // TTL RabbitMQ 配置
    private static final String TTL_EXCHANGE_NAME = "demo.ttl.exchange";
    private static final String TTL_ROUTING_KEY = "demo.ttl";
    // Retry RabbitMQ配置
    private static final String RETRY_EXCHANGE_NAME = "demo.retry.exchange";
    private static final String RETRY_ROUTING_KEY = "demo.retry";
    private final RabbitTemplate rabbitTemplate;

    public DemoMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送消息。
     *
     * @param message 要发送到 RabbitMQ 的消息内容
     */
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(DEMO_EXCHANGE_NAME, DEMO_ROUTING_KEY, message);
        log.info("Producer已发送消息：{};", message);
    }

    /**
     * 发送消息给 TTL RabbitMQ。
     *
     * @param message 要发送到 RabbitMQ 的消息内容
     */
    public void sendTtlMessage(String message) {
        rabbitTemplate.convertAndSend(TTL_EXCHANGE_NAME, TTL_ROUTING_KEY, message);
        log.info("Producer已发送给TTL RabbitMQ的消息：{};", message);
    }

    /**
     * 把处理失败的消息发送到 Retry Exchange,Retry Queue 会让消息等待 xxx(去看配置类) 秒，然后通过 DLX 重新发送回正常业务 Exchange。
     *
     * @param message 要发送到 RabbitMQ 的消息内容
     */
    public void sendRetryMessage(String message) {
        rabbitTemplate.convertAndSend(RETRY_EXCHANGE_NAME, RETRY_ROUTING_KEY, message);
        log.info("Producer 已发送消息到 Retry Queue：{};", message);
    }

}

package com.example.producer;

import com.example.mapper.PublisherMessageRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
    private final PublisherMessageRecordMapper publisherMessageRecordMapper;

    public DemoMessageProducer(RabbitTemplate rabbitTemplate, PublisherMessageRecordMapper publisherMessageRecordMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.publisherMessageRecordMapper = publisherMessageRecordMapper;
    }

    /**
     * 发送消息。
     *
     * @param message 要发送到 RabbitMQ 的消息内容
     */
    public void sendMessage(String message) {
        String messageId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(messageId);
        publisherMessageRecordMapper.insertPending(messageId, message, DEMO_EXCHANGE_NAME, DEMO_ROUTING_KEY);
        log.info("\n生产者消息已登记为 PENDING，messageId={}", messageId);
        rabbitTemplate.convertAndSend(DEMO_EXCHANGE_NAME, DEMO_ROUTING_KEY, message, rabbitMessage -> {
            rabbitMessage.getMessageProperties().setMessageId(messageId);
            return rabbitMessage;
        }, correlationData);
        log.info("\nProducer已发送消息：{};\nmessageId= {}；", message, messageId);
    }

    /**
     * 发送消息给 TTL RabbitMQ。
     *
     * @param message 要发送到 RabbitMQ 的消息内容
     */
    public void sendTtlMessage(String message) {
        rabbitTemplate.convertAndSend(TTL_EXCHANGE_NAME, TTL_ROUTING_KEY, message);
        log.info("\nProducer已发送给TTL RabbitMQ的消息：{};", message);
    }

    /**
     * 把处理失败的消息发送到 Retry Exchange,Retry Queue 会让消息等待 xxx(去看配置类) 秒，然后通过 DLX 重新发送回正常业务 Exchange。
     *
     * @param message 要发送到 RabbitMQ 的消息内容
     */
    public void sendRetryMessage(String message) {
        // TODO 后续统一消息发送方式，确保所有业务消息都有 messageId
        rabbitTemplate.convertAndSend(RETRY_EXCHANGE_NAME, RETRY_ROUTING_KEY, message);
        log.info("\nProducer 已发送消息到 Retry Queue：{};", message);
    }

    /**
     * 幂等测试专用：
     * 允许手动指定 messageId，
     * 这样我们可以故意发送两次相同 ID 的消息。
     */
    public void sendMessageWithId(String message, String messageId) {
        rabbitTemplate.convertAndSend(DEMO_EXCHANGE_NAME, DEMO_ROUTING_KEY, message, rabbitMessage -> {
            rabbitMessage.getMessageProperties().setMessageId(messageId);
            return rabbitMessage;
        });
        log.info("\nProducer发送幂等测试消息：{}，messageId={}", message, messageId);
    }

    public void sendUnroutableMessage(String message) {
        String messageId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(messageId);
        //使用错误的Routing Key
        String wrongRoutingKey = "demo.hello.WRONG";
        publisherMessageRecordMapper.insertPending(messageId, message, DEMO_EXCHANGE_NAME, wrongRoutingKey);
        rabbitTemplate.convertAndSend(DEMO_EXCHANGE_NAME, wrongRoutingKey, message, rabbitMessage -> {
            rabbitMessage.getMessageProperties().setMessageId(messageId);
            return rabbitMessage;
        }, correlationData);
        log.info("\nProducer发送路由失败测试消息：{}，messageId={}，routingKey={}", message, messageId, wrongRoutingKey);
    }
}

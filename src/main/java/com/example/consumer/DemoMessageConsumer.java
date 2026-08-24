package com.example.consumer;

import com.example.entity.MessageRecord;
import com.example.repository.MessageRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class DemoMessageConsumer {
    private final MessageRecordRepository messageRecordRepository;

    public DemoMessageConsumer(MessageRecordRepository messageRecordRepository) {
        this.messageRecordRepository = messageRecordRepository;
    }

    @RabbitListener(queues = "demo.hello.queue", containerFactory = "retryRabbitListenerContainerFactory",concurrency = "2")
    public void receiveMessage(String messageBody, Message message) throws InterruptedException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String messageId = message.getMessageProperties().getMessageId();
        Boolean redelivered = message.getMessageProperties().isRedelivered();
        log.info("Consumer 收到消息:{};\n当前 deliveryTag:{};\n当前 messageId：{}；\n是否重新投递：{};\n开始处理业务，等待2秒...", messageBody, deliveryTag, messageId, redelivered);
        if (messageRecordRepository.existsByMessageId(messageId)) {
            log.warn("检测到重复消息，跳过业务处理，messageId={}", messageId);
            return;
        }
        Thread.sleep(2000);
        if (messageBody.contains("retry")) {
            //如果消息包含 retry，我故意让业务失败,显示我配置的retry和配置方式。
            System.out.println("业务处理失败，准备交给 Spring Retry");
            throw new RuntimeException("模拟业务处理失败");
        }
        log.info("业务处理成功，Spring 自动 ACK：{}", messageBody);
        MessageRecord messageRecord = new MessageRecord();
        messageRecord.setMessageId(messageId);
        messageRecord.setStatus("SUCCESS");
        messageRecord.setCreateTime(LocalDateTime.now());
        messageRecordRepository.save(messageRecord);
    }
}

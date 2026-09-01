package com.example.consumer;

import com.example.service.IdempotentMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DemoMessageConsumer {
    private final IdempotentMessageService idempotentMessageService;

    public DemoMessageConsumer(IdempotentMessageService idempotentMessageService) {
        this.idempotentMessageService = idempotentMessageService;
    }

    @RabbitListener(queues = "demo.hello.queue", containerFactory = "retryRabbitListenerContainerFactory", concurrency = "2")
    public void receiveMessage(String messageBody, Message message) throws InterruptedException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String messageId = message.getMessageProperties().getMessageId();
        Boolean redelivered = message.getMessageProperties().isRedelivered();
        log.info(
                "\nConsumer 收到消息:{};\n当前 deliveryTag:{};\n当前 messageId：{}；\n是否重新投递：{};\n开始处理业务，等待2秒...",
                messageBody,
                deliveryTag,
                messageId,
                redelivered
        );
        idempotentMessageService.process(messageId, messageBody);
    }
}

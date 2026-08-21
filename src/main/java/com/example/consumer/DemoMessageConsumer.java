package com.example.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DemoMessageConsumer {
    @RabbitListener(queues = "demo.hello.queue", containerFactory = "retryRabbitListenerContainerFactory")
    public void receiveMessage(String messageBody, Message message) throws InterruptedException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        Boolean redelivered = message.getMessageProperties().isRedelivered();
        log.info("Consumer 收到消息:{};\n当前 deliveryTag:{};\n是否重新投递：{};\n开始处理业务，等待2秒...", messageBody, deliveryTag, redelivered);
        Thread.sleep(2000);
        //如果消息包含 retry，我故意让业务失败,显示我配置的retry和配置方式。
        if (messageBody.contains("retry")) {
            System.out.println("业务处理失败，准备交给 Spring Retry");
            throw new RuntimeException("模拟业务处理失败");
        }
        log.info("业务处理成功，Spring 自动 ACK：{}", messageBody);
    }
}

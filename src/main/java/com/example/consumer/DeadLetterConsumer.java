package com.example.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DeadLetterConsumer {
    @RabbitListener(queues = "demo.dlq.queue")
    public void receiveDeadMessage(Message message) {
        List<Map<String, ?>> xDeathHeader = message.getMessageProperties().getXDeathHeader();
        if (xDeathHeader != null && !xDeathHeader.isEmpty()) {
            Map<String, ?> xDeath = xDeathHeader.get(0);
            System.out.println("死亡原因:" + xDeath.get("reason"));
            System.out.println("死亡次数:" + xDeath.get("count"));
            System.out.println("原 Queue：" + xDeath.get("queue"));
            System.out.println("原 Routing Key:" + xDeath.get("routing-keys"));
            System.out.println("死亡时间：" + xDeath.get("time"));
            /**
             * 1. 读取 x-death  √
             * 2. 取 reason  √
             * 3. 根据 reason 做分支  rejected、expired、maxlen、delivery_limit  √
             * 4. 输出不同的处理日志  √
             * */
            System.out.println("=========================================");
            System.out.println("收到死信消息："+new String(message.getBody(), StandardCharsets.UTF_8));
            Object reason = xDeath.get("reason");
            if ("rejected".equals(reason)) {
                log.info("死信原因：消费者拒绝消息，并且 requeue=false;");
            } else if ("expired".equals(reason)) {
                log.info("死信原因：消息 TTL 到期");
            } else if ("maxlen".equals(reason)) {
                log.info("死信原因：队列超过最大长度限制");
            } else if ("delivery_limit".equals(reason)) {
                log.info("死信原因：Quorum Queue 的消息投递次数超过限制");
            } else {
                log.warn("未知死信原因：{}", reason);
            }


        }
    }
}

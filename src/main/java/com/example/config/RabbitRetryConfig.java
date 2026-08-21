package com.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Slf4j
@Configuration
public class RabbitRetryConfig {
    /**
     * --- Spring Retry 重试耗尽后的恢复策略。
     * - 当前策略：不直接进入最终 DLQ，而是先发送到 Retry Exchange，进入 Retry Queue 延迟一段时间以后再重试。
     */
    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return (message, cause) -> {
            long retryCount = message.getMessageProperties().getRetryCount();
            int maxRetryCount = 3;
            if (retryCount < maxRetryCount) {
                long nextRetryCount = retryCount + 1;
                Message retryMessage = MessageBuilder.fromMessage(message).build();
                retryMessage.getMessageProperties().setRetryCount(nextRetryCount);
                log.warn("原对象={}，新对象={}，retryCount={}→{}",
                        System.identityHashCode(message),
                        System.identityHashCode(retryMessage),
                        retryCount,
                        nextRetryCount
                );
                rabbitTemplate.send(RabbitTopologyConfig.RETRY_EXCHANGE, RabbitTopologyConfig.RETRY_ROUTING_KEY, retryMessage);
            } else {
                rabbitTemplate.send(RabbitTopologyConfig.DEAD_EXCHANGE, RabbitTopologyConfig.DEAD_ROUTING_KEY, message);
            }
        };
    }

    /**
     * --- Spring Retry 拦截器。
     * - stateless：使用无状态重试。
     * - maxAttempts(2)：总共执行 2 次。
     * - backOffOptions：第一次间隔 1000ms、multiplier = 1.0、最大间隔 5000ms
     * - recoverer 耗尽以后：执行 messageRecoverer。
     */
    @Bean
    public RetryOperationsInterceptor retryOperationsInterceptor(MessageRecoverer messageRecoverer) {
        return RetryInterceptorBuilder.
                stateless().
                maxAttempts(2).
                backOffOptions(1000, 1.0, 5000).
                recoverer(messageRecoverer).
                build();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory retryRabbitListenerContainerFactory(ConnectionFactory connectionFactory, RetryOperationsInterceptor retryOperationsInterceptor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        //设置RabbitMQ连接工厂
        factory.setConnectionFactory(connectionFactory);
        //当前还是 AUTO ACK。
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        //把我们刚刚自己创建的 RetryInterceptor 挂到这个 Listener Container Factory 上。
        factory.setAdviceChain(retryOperationsInterceptor);
        return factory;
    }
}

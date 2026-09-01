package com.example.controller;

import com.example.producer.DemoMessageProducer;
import com.example.service.PublisherRetryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoMessageController {
    private final DemoMessageProducer demoMessageProducer;
    private final PublisherRetryService publisherRetryService;

    public DemoMessageController(DemoMessageProducer demoMessageProducer, PublisherRetryService publisherRetryService) {
        this.demoMessageProducer = demoMessageProducer;
        this.publisherRetryService = publisherRetryService;
    }

    /**
     * 发送 RabbitMQ 消息。
     * 请求方式：POST
     * 请求地址：/api/demo/send
     * 参数：message
     * 例如：
     * POST /api/demo/send?message=HelloRabbitMQ
     *
     * @param message 前端传进来的消息     @return 返回发送结果
     */
    @PostMapping("/send")
    public String sendMessage(@RequestParam String message) {
        demoMessageProducer.sendMessage(message);
        return "消息发送成功：" + message;
    }

    /**
     * 发送 RabbitMQ 消息。
     * 请求方式：POST
     * 请求地址：/api/demo/send/ttlTest
     * 参数：message
     * 例如：
     * POST /api/demo/send-ttl?message=这是一个测试ttl的消息
     *
     * @param message 前端传进来的消息     @return 返回发送结果
     */
    @PostMapping("/send-ttl")
    public String sendTtlMessage(@RequestParam String message) {
        demoMessageProducer.sendTtlMessage(message);
        return "消息发送成功" + message;
    }

    /**
     * RabbitMQ 消息幂等测试接口。很爽的喔~~~小飞棍开罗
     * <p>
     * 可以手动传入固定 messageId，
     * 用于模拟同一条消息被重复投递。
     */
    @PostMapping("/send-idempotent")
    public String sendIdempotentMessage(@RequestParam String message, @RequestParam String messageId) {
        demoMessageProducer.sendMessageWithId(message, messageId);
        return "幂等测试消息发送成功：" + message + "，messageId=" + messageId;
    }

    /**
     * RabbitMQ 检测消息发送不成功的可靠性
     * <p>
     * 可以手动传入固定 message，用于模拟同一条消息被重复投递。
     */
    @PostMapping("/send-unroutable")
    public String sendUnroutableMessage(@RequestParam String message) {
        demoMessageProducer.sendUnroutableMessage(message);
        return "路由失败测试消息已发送：" + message;
    }


    @PostMapping("/retry-failed")
    public String retryFailedMessages() {
        publisherRetryService.retryFailedMessages();
        return "失败消息补偿任务已执行";
    }
}

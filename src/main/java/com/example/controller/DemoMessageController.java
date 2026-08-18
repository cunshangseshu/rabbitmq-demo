package com.example.controller;


import com.example.producer.DemoMessageProducer;
import jakarta.annotation.Resources;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoMessageController {
    /**
     * 注入我们刚刚创建的消息生产者。
     */
    private final DemoMessageProducer demoMessageProducer;

    /**
     * 构造器注入 Producer。
     *
     */
    public DemoMessageController(DemoMessageProducer demoMessageProducer) {
        this.demoMessageProducer = demoMessageProducer;
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

        /*
         * Controller 不直接操作 RabbitMQ，Controller 只调用 Producer。
         */
        demoMessageProducer.sendMessage(message);
        /*
         * 返回给 HTTP 客户端，注意：
         *  这个返回值不是 RabbitMQ 消息，RabbitMQ 消息已经在上面 Producer 中发送了。
         */
        return "消息发送成功：" + message;
    }
}

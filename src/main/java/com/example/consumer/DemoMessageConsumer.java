package com.example.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * RabbitMQ 消息消费者
 * 当前学习目标：
 * 使用 MANUAL 手动 ACK 模式。
 * 消息流程：
 * Queue
 * ↓
 * Consumer 收到消息
 * ↓
 * 执行业务代码
 * ↓
 * 成功：basicAck()
 * ↓
 * RabbitMQ 删除消息
 * <p>
 * 如果失败：
 * Consumer 自己决定：
 * basicNack()
 * 或
 * basicReject()
 * 并决定消息是否重新进入 Queue。
 */
@Component
public class DemoMessageConsumer {
    /**
     * ackMode = "MANUAL"
     * 表示：
     * Spring 不再自动帮我们确认消息，
     * 消费者需要自己调用 basicAck / basicNack / basicReject。
     */
    @RabbitListener(queues = "demo.hello.queue")
    public void receiveMessage(String messageBody, Message message, Channel channel) throws InterruptedException, IOException {
        /*
         * -- messageBody：
         * Spring 已经帮我们把 RabbitMQ 消息正文转换成 String。
         * -- message：这是 Spring AMQP 对一整条 RabbitMQ 消息的封装,它不仅包含消息正文，
         * 还包含：Routing Key、Exchange、Delivery Tag、Headers、Content Type、Redelivered等信息。
         */

        System.out.println("Consumer 收到消息" + messageBody);

        /*
         * deliveryTag：
         * RabbitMQ 给当前 Channel 中这次消息投递的编号。
         * Consumer ACK 的时候必须告诉 RabbitMQ：“我确认的是哪一次消息投递。”
         */
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        Boolean redelivered = message.getMessageProperties().isRedelivered();
        System.out.println("当前 deliveryTag:" + deliveryTag);
        System.out.println("是否重新投递：" + redelivered);
        /*
         * 为了方便观察 RabbitMQ Management，
         * 我们继续故意等待 10 秒。
         * 这 10 秒期间：Ready = 0   Unacked = 1
         */
        System.out.println("开始处理业务，等待3秒...");
        Thread.sleep(3000);

        /*
         * 如果消息包含 retry，我们故意让业务失败。
         */
        if (messageBody.contains("retry")) {
            System.out.println("业务处理失败，准备交给 Spring Retry");
            /*
             * -- 注意这里：不 basicAck、不 basicNack、不 basicReject，直接把异常抛出去。
             * -- Spring Retry 才能感知：“这次执行失败了，我应该重试。”
             */
            throw new RuntimeException("模拟业务处理失败");
        }
        /*
         * 如果代码能执行到这里，说明业务处理成功。
         * 所以手动 ACK。
         */
        //channel.basicAck(deliveryTag, false);

        /*
         * AUTO ACK 模式：
         * Listener 正常执行结束，Spring 自动帮我们确认消息。
         * 所以这里不再手动 basicAck。
         */
        System.out.println("业务处理成功，手动 ACK：" + messageBody);


        /*
         * 手动 ACK。
         * -- 参数1：deliveryTag
         *  告诉 RabbitMQ：我要确认哪条消息。
         * -- 参数2：multiple = false
         *      false：只确认当前这一条消息。
         *      true：批量确认当前 deliveryTag 以及之前未确认的消息。
         * 学习阶段我们固定使用 false。
         */
        //手动确认
        //channel.basicAck(deliveryTag, false);
        //System.out.println("消息处理成功，已手动 ACK：" + messageBody);

        // 自动确认
        /*
         * -- 参数1：deliveryTag
         * 表示拒绝哪一次消息投递。
         * -- 参数2：multiple = false
         * false：只处理当前这一条消息。
         * true：批量处理当前 deliveryTag 以及之前未确认的消息。
         * -- 参数3：requeue = true
         * true：告诉 RabbitMQ：
         *       “这条消息我没处理成功，请重新放回 Queue。”
         */
        //channel.basicNack(deliveryTag, false, true);//这行代码可以理解为：RabbitMQ，这条消息我没处理成功，不要删，重新给它排队。
        //channel.basicNack(deliveryTag, false, false);//这行代码可以理解为：这条消息我处理失败了，而且别再塞回来。
        //System.out.println("消息处理失败，NACK，并重新入队：" + messageBody);

        //basicReject方法：
        /*
         * basicReject(
         *      deliveryTag,
         *      requeue
         * )
         * -- 参数1：deliveryTag
         * 表示拒绝当前这次消息投递。
         * -- 参数2：requeue = true
         * true：
         *      消息重新进入 Queue。
         * false：
         *      消息不重新进入原 Queue。
         */
        //channel.basicReject(deliveryTag, true);
        //System.out.println("消息处理失败，Reject，并重新入队：" + messageBody);
    }
}

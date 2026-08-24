package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

//RabbitMQ 消息消费记录表
@Data
@Entity
@Table(name = "message_record", uniqueConstraints = {@UniqueConstraint(name = "uk_message_id", columnNames = "message_id")})
public class MessageRecord {
    //数据库主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * RabbitMQ 消息唯一 ID
     * <p>
     * 同一条业务消息，无论：
     * <p>
     * Spring Retry 重试
     * Retry Queue 再次投递
     * RabbitMQ 重新投递
     * <p>
     * messageId 都应该保持不变。
     */
    @Column(name = "message_id", nullable = false, length = 64)
    private String messageId;

    /**
     * 消费状态
     * <p>
     * 当前学习阶段先简单保存 SUCCESS。
     * <p>
     * 后面可以继续扩展：
     * <p>
     * PROCESSING
     * SUCCESS
     * FAILED
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /**
     * 消费成功时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;
}

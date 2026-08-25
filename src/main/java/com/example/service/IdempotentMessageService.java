package com.example.service;

import com.example.entity.MessageRecord;
import com.example.repository.MessageRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class IdempotentMessageService {
    private final MessageRecordRepository messageRecordRepository;

    public IdempotentMessageService(MessageRecordRepository messageRecordRepository) {
        this.messageRecordRepository = messageRecordRepository;
    }

    /**
     * 并发安全的幂等消费。
     * 核心思想：
     * 1. 先尝试 INSERT message_record；
     * 2. 利用数据库 UNIQUE(message_id) 竞争；
     * 3. 谁 INSERT 成功，谁才有资格执行业务；
     * 4. 业务失败，整个事务回滚；
     * 5. 重复消息 INSERT 失败，则不执行业务。
     */
    @Transactional
    public void process(String messageId, String messageBody) throws InterruptedException {
        MessageRecord messageRecord = new MessageRecord();
        messageRecord.setMessageId(messageId);
        messageRecord.setStatus("PROCESSING");
        messageRecord.setCreateTime(LocalDateTime.now());
        messageRecordRepository.saveAndFlush(messageRecord);
        log.info("成功抢占 messageId={}，开始执行真正业务", messageId);
        // 模拟真正业务处理
        Thread.sleep(2000);
        // 模拟业务异常
        if (messageBody.contains("retry")) {
            log.error("业务处理失败，事务准备回滚，messageId={}", messageId);
            throw new RuntimeException("模拟业务处理失败");
        }
        messageRecord.setStatus("SUCCESS");
        messageRecordRepository.save(messageRecord);
        log.info("业务处理成功，事务准备提交，messageId={}", messageId);
    }
}

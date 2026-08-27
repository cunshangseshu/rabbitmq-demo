package com.example.service;

import com.example.mapper.MessageRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class IdempotentMessageService {
    private final MessageRecordMapper messageRecordMapper;

    public IdempotentMessageService(MessageRecordMapper messageRecordMapper) {
        this.messageRecordMapper = messageRecordMapper;
    }

    //改造一下，用mybatis + sql的原子性 按照 ignore的反馈1或0的结果进行操作；
    // ignore的sql语句可以在xml内看到详情；
    @Transactional
    public void process(String messageId, String messageBody) throws InterruptedException {
        // 尝试原子抢占 messageId
        if (messageRecordMapper.tryAcquireMessage(messageId) == 0) {
            log.warn("重复消息，跳过业务处理，messageId={}", messageId);
            return;
        }
        log.info("原子抢占成功，开始执行真正业务，messageId={}", messageId);
        // 模拟真正业务处理
        Thread.sleep(2000);
        // 模拟业务异常
        if (messageBody.contains("retry")) {
            log.error("业务处理失败，事务准备回滚，messageId={}", messageId);
            throw new RuntimeException("模拟业务处理失败");
        }
        //该markSuccess的sql语句和参数作用去看xml，有对应的方法id值
        messageRecordMapper.markSuccess(messageId);
        log.info("业务处理成功，messageId={}，状态修改为 SUCCESS，事务准备提交", messageId);
    }
}

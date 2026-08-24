package com.example.repository;

import com.example.entity.MessageRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//RabbitMQ 消费记录 Repository
public interface MessageRecordRepository extends JpaRepository<MessageRecord, Long> {
    //根据 messageId 查询消费记录
    Optional<MessageRecord> findByMessageId(String messageId);

    //判断某个 messageId 是否已经存在
    boolean existsByMessageId(String messageId);
}

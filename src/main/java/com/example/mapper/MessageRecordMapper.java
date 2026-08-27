package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageRecordMapper {
    int tryAcquireMessage(@Param("messageId") String messageId);

    void markSuccess(@Param("messageId") String messageId);
}

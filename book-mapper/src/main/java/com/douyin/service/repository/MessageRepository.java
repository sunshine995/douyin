package com.douyin.service.repository;

import com.douyin.service.mo.MessageMo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MessageRepository extends MongoRepository<MessageMo, String> {
}

package com.douyin.service.mapper;

import com.douyin.service.pojo.Comment;
import com.douyin.service.my.mapper.MyMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentMapper extends MyMapper<Comment> {
}
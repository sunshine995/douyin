package com.douyin.service.mapper;

import com.douyin.service.pojo.Fans;
import com.douyin.service.my.mapper.MyMapper;
import org.springframework.stereotype.Repository;

// 加此注解可以消除错误
@Repository
public interface FansMapper extends MyMapper<Fans> {
}
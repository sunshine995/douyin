package com.douyin.service.mapper;

import com.douyin.service.pojo.Users;
import com.douyin.service.my.mapper.MyMapper;
import org.springframework.stereotype.Repository;

// 这个加不加无所谓，不影响运行
@Repository
public interface UsersMapper extends MyMapper<Users> {
}
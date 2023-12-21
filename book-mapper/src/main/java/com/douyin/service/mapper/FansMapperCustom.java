package com.douyin.service.mapper;

import com.douyin.service.my.mapper.MyMapper;
import com.douyin.service.pojo.Fans;
import com.douyin.service.vo.FansVO;
import com.douyin.service.vo.VlogerVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

// 加此注解可以消除错误
@Repository
public interface FansMapperCustom extends MyMapper<Fans> {

    // 传入一个map  应该适合parameterType相对应
    public List<VlogerVO> queryMyFollows(@Param("paramMap")Map<String, Object> map);

    // 传入一个map  应该适合parameterType相对应
    public List<FansVO> queryMyFans(@Param("paramMap")Map<String, Object> map);
}
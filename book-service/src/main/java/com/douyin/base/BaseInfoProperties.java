package com.douyin.base;

import com.douyin.service.grace.PagedGridResult;
import com.douyin.service.utils.RedisOperator;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BaseInfoProperties {

    @Autowired
    public RedisOperator redis;

    public static final Integer COMMENT_StART_PAGE = 1;
    public static final Integer COMMENT_PAGE_SIZE = 10;
    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_INFO = "redis_user_info";
    public static final String MOBILE_SMSCODE = "mobile:smscode";

        // 我的关注总数
    public static final String REDIS_MY_FOLLOWS_COUNTS = "redis_my_follows_counts";
    // 我的粉丝总数
    public static final String REDIS_MY_FANS_COUNTS = "redis_my_fans_counts";

    // 博主和粉丝的关联关系
    public static final String REDIS_FANS_AND_VLOGGER_RELATIONSHIP = "redis_fans_and_vlogger_relationship";
    // 视频和发布者获赞数
    public static final String REDIS_VLOG_BE_LIKED_COUNTS = "redis_vlog_be_liked_counts";
    public static final String REDIS_VLOGER_BE_LIKED_COUNTS = "redis_vloger_be_liked_counts";

    // 用户是否点赞、喜欢视频，取代数据库的关联关系 1：喜欢， 0：不喜欢（默认）
    public static final String REDIS_USER_LIKE_VLOG = "redis_user_like_vlog";

    // 短视频的评论总数
    public static final String REDIS_VLOG_COMMENT_COUNTS = "redis_vlog_comment_counts";

    // 短视频的评论点赞总数
    public static final String REDIS_VLOG_COMMENT_LIKED_COUNTS = "redis_vlog_comment_liked_counts";

    //用户是否喜欢过
    public static final String REDIS_USER_COMMENT_LIKED = "redis_user_comment_liked";
//    public Map<String, String> getErrors(BindingResult bindingResult){
//        Map<String, String> map = new HashMap<>();
//        List<FieldError> errorList = bindingResult.getFieldErrors();
//        for (FieldError ff : errorList){
//            // 字段属性，错误所对应的字段属性名
//            String field = ff.getField();
//            // 错误的信息
//            String message = ff.getDefaultMessage();
//            map.put(field, message);
//        }
//        return map;
//    }

    public PagedGridResult setterPagedGrid(List<?> list,
                                           Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(list);
        gridResult.setPage(page);
        gridResult.setRecords(pageList.getTotal());
        gridResult.setTotal(pageList.getPages());
        return gridResult;
    }
}

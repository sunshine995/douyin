//package com.douyin.service.controller;
//
//import com.douyin.service.utils.RedisOperator;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class BaseInfoProperties {
//
//    @Autowired
//    public RedisOperator redis;
//
//    public static final Integer COMMENT_StART_PAGE = 1;
//    public static final Integer COMMENT_PAGE_SIZE = 10;
//    public static final String REDIS_USER_TOKEN = "redis_user_token";
//    public static final String REDIS_USER_INFO = "redis_user_info";
//
//        // 我的关注总数
//    public static final String REDIS_MY_FOLLOWS_COUNTS = "redis_my_follows_counts";
//    // 我的粉丝总数
//    public static final String REDIS_MY_FANS_COUNTS = "redis_my_fans_counts";
//
//    // 视频和发布者获赞数
//    public static final String REDIS_VLOG_BE_LIKED_COUNTS = "redis_vlog_be_liked_counts";
//    public static final String REDIS_VLOGER_BE_LIKED_COUNTS = "redis_vloger_be_liked_counts";
//
////    public Map<String, String> getErrors(BindingResult bindingResult){
////        Map<String, String> map = new HashMap<>();
////        List<FieldError> errorList = bindingResult.getFieldErrors();
////        for (FieldError ff : errorList){
////            // 字段属性，错误所对应的字段属性名
////            String field = ff.getField();
////            // 错误的信息
////            String message = ff.getDefaultMessage();
////            map.put(field, message);
////        }
////        return map;
////    }
//}

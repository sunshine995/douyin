package com.douyin.service;



import java.util.Map;

public interface MsgService {


    /**
     * 创建消息
     * @param fromUserId
     * @param toUserId
     * @param type
     * @param msgContent
     */
    public void createMsg(String fromUserId,
                          String toUserId,
                          Integer type,
                          Map msgContent);
}

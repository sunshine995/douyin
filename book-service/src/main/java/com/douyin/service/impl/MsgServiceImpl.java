package com.douyin.service.impl;

import com.douyin.service.MsgService;
import com.douyin.service.UserService;
import com.douyin.service.mo.MessageMo;
import com.douyin.service.pojo.Users;
import com.douyin.service.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

@Service
public class MsgServiceImpl implements MsgService {

    @Resource
    private MessageRepository messageRepository;
    @Autowired
    private UserService userService;
    @Override
    public void createMsg(String fromUserId, String toUserId, Integer type, Map msgContent) {

        MessageMo messageMo = new MessageMo();


        messageMo.setFromUserId(fromUserId);
        Users fromUser = userService.queryUser(fromUserId);
        messageMo.setFromFace(fromUser.getFace());
        messageMo.setFromNickname(fromUser.getNickname());

        messageMo.setToUserId(toUserId);

        messageMo.setMsgType(type);
        if (msgContent != null){
            messageMo.setMsgContent(msgContent);
        }
        messageMo.setCreatTime(new Date());

        messageRepository.save(messageMo);
    }
}

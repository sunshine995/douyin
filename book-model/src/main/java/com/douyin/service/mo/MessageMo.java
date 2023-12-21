package com.douyin.service.mo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.Date;
import java.util.Map;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor

@Document("message")
public class MessageMo {

    @Id
    private String id; // 消息主键id

    @Field("fromUserId")
    private String fromUserId;  // 消息来自的用户id
    @Field("fromNickname")
    private String fromNickname; //  消息来自的用户昵称
    @Field("fromFace")
    private String fromFace; //   消息来自的用户头像

    @Field("toUserId")
    private String toUserId; // 消息接受者的用户Id

    @Field("msgType")
    private Integer msgType; // 消息的枚举

    @Field("msgContent")
    private Map msgContent; // 消息内容

    @Field("creatTime")
    private Date creatTime; // 消息创建时间(排序)


}

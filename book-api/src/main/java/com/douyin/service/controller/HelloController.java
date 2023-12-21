package com.douyin.service.controller;

import com.douyin.service.grace.GraceJSONResult;
import com.douyin.service.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @Autowired
    private SMSUtils smsUtils;

    @GetMapping("sms")
    public Object sms() throws Exception{
        String code = "123456";
        //smsUtils.sendSMS("17513366869", code);
        return GraceJSONResult.ok("Hello Springboot");
    }
}

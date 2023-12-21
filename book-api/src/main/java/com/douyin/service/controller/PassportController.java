package com.douyin.service.controller;

import com.douyin.base.BaseInfoProperties;
import com.douyin.service.UserService;
import com.douyin.service.bo.RegisterLoginBo;
import com.douyin.service.grace.GraceJSONResult;
import com.douyin.service.grace.ResponseStatusEnum;
import com.douyin.service.pojo.Users;
import com.douyin.service.utils.IPUtil;
import com.douyin.service.utils.SMSUtils;
import com.douyin.service.vo.UsersVo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@Slf4j
@Api(tags = "PassportController 通信接口模块")
@RequestMapping("passport")
public class PassportController extends BaseInfoProperties {


    @Autowired
    private SMSUtils smsUtils;

    @Autowired
    private UserService userService;


    @PostMapping("getSMSCode")
    public GraceJSONResult sms(@RequestParam String mobile,
                               HttpServletRequest request) throws Exception {
        if (StringUtils.isBlank(mobile)) {
            return GraceJSONResult.ok();
        }
        // TODO 获得用户ip, 根据用户IP限制用户在60秒内只能获得一次验证码
        String userIp = IPUtil.getRequestIp(request);

        redis.setnx60s(MOBILE_SMSCODE + ":" + userIp, userIp);

        String code = (int) ((Math.random() * 9 + 1) * 100000) + "";
        try {
            smsUtils.sendSMS(mobile, code);
            redis.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);
            log.info(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return GraceJSONResult.ok();
    }

//通过valid开启注解
    @PostMapping("login")
    public GraceJSONResult login(@Valid @RequestBody RegisterLoginBo registerLoginBo,
 //                                BindingResult result, 对代码有侵略性
                                 HttpServletRequest request) throws Exception {
        // 0. 判断BindingResult中是否存在错误，存在错误则需要返回前端
//        if (result.hasErrors()){
//            return GraceJSONResult.errorMap(getErrors(result));
//        }

        //  首先拿到手机号，和密码
        String mobile = registerLoginBo.getMobile();
        String code = registerLoginBo.getSmsCode();

        // 1. 从redis中拿到验证码进行校验是否匹配
        String rediscode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(rediscode) || !rediscode.equalsIgnoreCase(code)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        // 2. 查询数据库判断用户是否存在
        Users user = userService.queryMobileIsExist(mobile);
        if (user == null){
            // 如果用户为空，说明没有注册过，则为null，需要进行注册
            user = userService.registerMobile(mobile);
        }

        // 3.如果不为空，则需要下面的业务，可以保存用户会话信息
        String uToken = UUID.randomUUID().toString();
        // 不设置过期时间
        redis.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken);

        // 4.用户登录成功之后，删除redis中的短信验证码，只能够用一次
        redis.del(MOBILE_SMSCODE + ":" + mobile);

        // 前段是要获取用户信息，包括token令牌
        UsersVo usersVo = new UsersVo();
        BeanUtils.copyProperties(user, usersVo);
        usersVo.setUserToken(uToken);

        return GraceJSONResult.ok(usersVo);
    }

    @PostMapping("logout")
    public GraceJSONResult logout(@RequestParam String userId,
                               HttpServletRequest request) throws Exception {
        // 后端只要清楚用户的token信息即可， 前端也需要app本地的用户信息和缓存
        redis.del(REDIS_USER_TOKEN + ":" + userId);
        return GraceJSONResult.ok();
    }


}
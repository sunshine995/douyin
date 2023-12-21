package com.douyin.service.controller;

import com.douyin.base.BaseInfoProperties;
import com.douyin.service.FansService;
import com.douyin.service.UserService;
import com.douyin.service.grace.GraceJSONResult;
import com.douyin.service.grace.ResponseStatusEnum;
import com.douyin.service.pojo.Users;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "FansController粉丝相关业务接口")
@RestController
@RequestMapping("fans")
public class FansController extends BaseInfoProperties {
    @Autowired
    private FansService fansService;
    @Autowired
    private UserService userService;

    @PostMapping("follow")
    public GraceJSONResult follow(@RequestParam String myId,
                                  @RequestParam String vlogerId){
        //System.out.println("粉丝借口");
        // 判断两个id不能为空
        if (StringUtils.isBlank(myId) || StringUtils.isBlank(vlogerId)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
        }

        // 判断用户自己不能管住自己
        if (myId.equalsIgnoreCase(vlogerId)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
        }
        // 判断两个id对应的用户是够存在
        Users myInfo = userService.queryUser(myId);
        Users vloger = userService.queryUser(vlogerId);
        // fixme: 两个用户的是分开判断好还是一起判断好
        if (myInfo == null || vloger == null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        }
        // 保存粉丝关系到数据库中
        fansService.doFollow(myId, vlogerId);

        // 博主的粉丝数 + 1. 我的关注数 + 1；
        redis.increment(REDIS_MY_FOLLOWS_COUNTS + ":" + myId, 1);
        redis.increment(REDIS_MY_FANS_COUNTS + ":" + vlogerId, 1);
        // 我和博主的关联关系，依赖redis， 不要存储到数据库中，避免db的性能瓶颈
        redis.set(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" +vlogerId, "1");
        return GraceJSONResult.ok();
    }

    @PostMapping("cancel")
    public GraceJSONResult cancel(@RequestParam String myId,
                                  @RequestParam String vlogerId){
        // 删除业务的执行
        fansService.doCancel(myId, vlogerId);

        // 博主的粉丝数 + 1. 我的关注数 + 1；
        redis.decrement(REDIS_MY_FOLLOWS_COUNTS + ":" + myId, 1);
        redis.decrement(REDIS_MY_FANS_COUNTS + ":" + vlogerId, 1);
        // 我和博主的关联关系，依赖redis， 不要存储到数据库中，避免db的性能瓶颈
        redis.del(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" +vlogerId);
        return GraceJSONResult.ok();
    }

    @GetMapping("queryDoIFollowVloger")
    public GraceJSONResult queryDoIFollowVloger(@RequestParam String myId,
                                  @RequestParam String vlogerId){

        return GraceJSONResult.ok(fansService.queryDoIFollowVloger(myId, vlogerId));
    }


    @GetMapping("queryMyFollows")
    public GraceJSONResult queryMyFollows(@RequestParam String myId,
                                          @RequestParam Integer page ,
                                          @RequestParam Integer pageSize){

        return GraceJSONResult.ok(fansService.queryMyFollows(myId,page,pageSize));
    }

    @GetMapping("queryMyFans")
    public GraceJSONResult queryMyFans(@RequestParam String myId,
                                          @RequestParam Integer page ,
                                          @RequestParam Integer pageSize){

        return GraceJSONResult.ok(fansService.queryMyFans(myId,page,pageSize));
    }
}

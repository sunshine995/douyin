package com.douyin.service.controller;

import com.douyin.base.BaseInfoProperties;
import com.douyin.service.CommenService;
import com.douyin.service.bo.CommentBO;
import com.douyin.service.grace.GraceJSONResult;
import com.douyin.service.vo.CommentVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Slf4j
@RestController
@Api(tags = "CommentController  评论接口")
@RequestMapping("comment")
public class CommentController extends BaseInfoProperties {
    @Autowired
    private CommenService commenService;

    @PostMapping("creat")
    public GraceJSONResult creat(@RequestBody @Valid CommentBO commentBO) throws Exception{

        CommentVO commentVO = commenService.createComment(commentBO);

        return GraceJSONResult.ok(commentVO);
    }

    @PostMapping("counts")
    public GraceJSONResult counts(@RequestParam String vlogId) throws Exception{

        String countsComment = redis.get(REDIS_VLOG_COMMENT_COUNTS + ":" + vlogId);

        if (StringUtils.isBlank(countsComment)){
            countsComment = "0";
        }
        return GraceJSONResult.ok(Integer.valueOf(countsComment));
    }
    @PostMapping("list")
    public GraceJSONResult list(@RequestParam String vlogId,
                                @RequestParam(defaultValue = "") String userId,
                                @RequestParam Integer page,
                                @RequestParam Integer pageSize) throws Exception{


        return GraceJSONResult.ok(commenService.queryVlogComments(vlogId,userId,page,pageSize));
    }

    @DeleteMapping("delete")
    public GraceJSONResult delete(@RequestParam String commentUserId,
                                @RequestParam String commentId,
                                @RequestParam String vlogId) throws Exception{

        commenService.delete(commentId, commentUserId, vlogId);

        return GraceJSONResult.ok();
    }

    @PostMapping("unlike")
    public GraceJSONResult unlike(@RequestParam String userId,
                                @RequestParam String commentId) throws Exception{

        // 故意犯错， bigkey（可以加一个路由）
        redis.decrementHash(REDIS_VLOG_COMMENT_LIKED_COUNTS, commentId, 1);
        // 针对某一个对象设置一个key，这个key的值或者对象是什么
        redis.hdel(REDIS_USER_COMMENT_LIKED, userId + ":" + commentId);

        return GraceJSONResult.ok();
    }

    @PostMapping("like")
    public GraceJSONResult like(@RequestParam String userId,
                                @RequestParam String commentId) throws Exception{

        // 故意犯错， bigkey（可以加一个路由）
        redis.incrementHash(REDIS_VLOG_COMMENT_LIKED_COUNTS, commentId, 1);
        // 针对某一个对象设置一个key，这个key的值或者对象是什么
        redis.setHashValue(REDIS_USER_COMMENT_LIKED, userId + ":" + commentId, "1");
        return GraceJSONResult.ok();
    }


}

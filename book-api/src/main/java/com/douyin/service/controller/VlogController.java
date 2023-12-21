package com.douyin.service.controller;

import com.douyin.base.BaseInfoProperties;
import com.douyin.enums.YesOrNo;
import com.douyin.service.VlogService;
import com.douyin.service.bo.VlogBO;
import com.douyin.service.grace.GraceJSONResult;
import com.douyin.service.grace.PagedGridResult;
import com.douyin.service.vo.IndexVlogVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "VlogController短视频相关业务接口")
@RestController
@RequestMapping("vlog")
public class VlogController extends BaseInfoProperties{
    @Autowired
    private VlogService vlogService;

    @PostMapping("publish")
    public GraceJSONResult publish(@RequestBody VlogBO vlogBO) throws Exception{
        // 作业校验
        vlogService.creatVlog(vlogBO);
        return GraceJSONResult.ok();
    }
    @GetMapping("detail")
    public GraceJSONResult detail(@RequestParam(defaultValue = "") String userId,
                                     @RequestParam String vlogId
                                     ) throws Exception{
        //System.out.println(vlogId);
        IndexVlogVO vlogvo = vlogService.getVlogDetailById(userId, vlogId);
        // 作业校验
        //PagedGridResult list = vlogService.getIndexVlogList(search, page, pageSize);
        return GraceJSONResult.ok(vlogvo);
    }

    @GetMapping("indexList")
    public GraceJSONResult indexList(@RequestParam String userId,
                                    @RequestParam(defaultValue = "") String search,
                                     @RequestParam Integer page,
                                     @RequestParam Integer pageSize) throws Exception{
        if (page == null){
            page = BaseInfoProperties.COMMENT_StART_PAGE;
        }
        if (pageSize == null){
            pageSize = BaseInfoProperties.COMMENT_PAGE_SIZE;
        }
        // 作业校验
        PagedGridResult list = vlogService.getIndexVlogList(userId, search, page, pageSize);
        return GraceJSONResult.ok(list);
    }

    @PostMapping("changeToPrivate")
    public GraceJSONResult changeToPrivate(@RequestParam String userId,
                                           @RequestParam String vlogId) throws Exception{
        // 1, 是私密
        vlogService.changeToPrivateOrPublic(userId, vlogId, YesOrNo.YES.type);
        return GraceJSONResult.ok();
    }

    @PostMapping("changeToPublic")
    public GraceJSONResult changeToPublic(@RequestParam String userId,
                                           @RequestParam String vlogId) throws Exception{
        // 0, 是公开
        vlogService.changeToPrivateOrPublic(userId, vlogId, YesOrNo.NO.type);
        return GraceJSONResult.ok();
    }



    @GetMapping("myPublicList")
    public GraceJSONResult myPublicList(@RequestParam String userId,
                                  @RequestParam Integer page,
                                  @RequestParam Integer pageSize
    ) throws Exception{
        if (page == null){
            page = BaseInfoProperties.COMMENT_StART_PAGE;
        }
        if (pageSize == null){
            pageSize = BaseInfoProperties.COMMENT_PAGE_SIZE;
        }
       // 根据有userId查询用户的
        PagedGridResult pagedGridResult = vlogService.queryMyVlog(userId, page, pageSize, YesOrNo.NO.type);
        return GraceJSONResult.ok(pagedGridResult);
    }
//myPrivateList

    @GetMapping("myPrivateList")
    public GraceJSONResult myPrivateList(@RequestParam String userId,
                                  @RequestParam Integer page,
                                  @RequestParam Integer pageSize
    ) throws Exception{
        if (page == null){
            page = BaseInfoProperties.COMMENT_StART_PAGE;
        }
        if (pageSize == null){
            pageSize = BaseInfoProperties.COMMENT_PAGE_SIZE;
        }
        // 根据有userId查询用户的
        PagedGridResult pagedGridResult = vlogService.queryMyVlog(userId, page, pageSize, YesOrNo.YES.type);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @PostMapping("like")
    public GraceJSONResult like(@RequestParam String userId,
                                @RequestParam String vlogerId,
                                          @RequestParam String vlogId) throws Exception{
        // 我点赞的视频，关联关系保存在数据库
        vlogService.userLikeVlog(userId, vlogId);
        // 点赞后，视频和视频发布者的点赞都回累加 1
        redis.increment(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId,1);
        redis.increment(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + vlogerId, 1);

        // 我点赞的视频，需要在redis中保存关联关系（判断你点进来之后红心是不是亮起来的）
        redis.set(REDIS_USER_LIKE_VLOG + ":" + userId + ":" + vlogId, "1");
        return GraceJSONResult.ok();
    }

    @PostMapping("unlike")
    public GraceJSONResult unlike(@RequestParam String userId,
                                @RequestParam String vlogerId,
                                @RequestParam String vlogId) throws Exception{
        // 我取消点赞的视频，关联关系保存在数据库
        vlogService.userUnLikeVlog(userId, vlogId);
        // 点赞后，视频和视频发布者的点赞都回累加 1
        redis.decrement(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId,1);
        redis.decrement(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + vlogerId, 1);

        // 我点赞的视频，需要在redis中保存关联关系（判断你点进来之后红心是不是亮起来的）
        redis.del(REDIS_USER_LIKE_VLOG + ":" + userId + ":" + vlogId);
        return GraceJSONResult.ok();
    }

    @PostMapping("totalLikedCounts")
    public GraceJSONResult totalLikedCounts(@RequestParam String vlogId) throws Exception{

        return GraceJSONResult.ok(vlogService.countLikeVlog(vlogId));
    }

    // 查询我点赞过的视频（公开的）
    @GetMapping("myLikedList")
    public GraceJSONResult myLikedList(@RequestParam String userId,
                                         @RequestParam Integer page,
                                         @RequestParam Integer pageSize
    ) throws Exception{
        if (page == null){
            page = BaseInfoProperties.COMMENT_StART_PAGE;
        }
        if (pageSize == null){
            pageSize = BaseInfoProperties.COMMENT_PAGE_SIZE;
        }
        // 根据有userId查询用户的
        PagedGridResult pagedGridResult = vlogService.getMyLikedVlogList(userId, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }

    // 查询我关注的视频博主的全部视频
    @GetMapping("followList")
    public GraceJSONResult followList(@RequestParam String myId,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize
    ) throws Exception{
        if (page == null){
            page = BaseInfoProperties.COMMENT_StART_PAGE;
        }
        if (pageSize == null){
            pageSize = BaseInfoProperties.COMMENT_PAGE_SIZE;
        }
        // 根据有userId查询用户的
        PagedGridResult pagedGridResult = vlogService.getMyFollowVlogList(myId, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }

    // 查询朋友的视频列表
    @GetMapping("friendList")
    public GraceJSONResult friendList(@RequestParam String myId,
                                      @RequestParam Integer page,
                                      @RequestParam Integer pageSize
    ) throws Exception{
        if (page == null){
            page = BaseInfoProperties.COMMENT_StART_PAGE;
        }
        if (pageSize == null){
            pageSize = BaseInfoProperties.COMMENT_PAGE_SIZE;
        }
        // 根据有userId查询用户的
        PagedGridResult pagedGridResult = vlogService.getMyFriendVlogList(myId, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }
}

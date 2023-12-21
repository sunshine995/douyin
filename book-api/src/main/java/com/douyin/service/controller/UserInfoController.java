package com.douyin.service.controller;

import com.douyin.base.BaseInfoProperties;
import com.douyin.enums.FileTypeEnum;
import com.douyin.enums.UserInfoModifyType;
import com.douyin.service.MinIOConfig;
import com.douyin.service.UserService;
import com.douyin.service.bo.UpdatedUserBo;
import com.douyin.service.grace.GraceJSONResult;
import com.douyin.service.grace.ResponseStatusEnum;
import com.douyin.service.pojo.Users;
import com.douyin.service.utils.MinIOUtils;
import com.douyin.service.vo.UsersVo;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@Api(tags = "用户信息接口模块")
@RequestMapping("userInfo")
public class UserInfoController extends BaseInfoProperties {
    @Autowired
    private UserService userService;

    @Autowired
    private MinIOConfig minIOConfig;

    @GetMapping("query")
    public GraceJSONResult query(@RequestParam String userId) throws Exception{
        Users user = userService.queryUser(userId);

        // 将获取的用户信息存储起来，
        UsersVo usersVo = new UsersVo();
        BeanUtils.copyProperties(user, usersVo);

        // 我关注的博主总数 （通过redis获取）
        String myFollowsCountsStr = redis.get(REDIS_MY_FOLLOWS_COUNTS + ":" + userId);

        // 我的粉丝总数
        String myFansCountsStr = redis.get(REDIS_MY_FANS_COUNTS + ":" + userId);
        // 用户获赞总数，视频 + 评论（点赞 / 喜欢）总和
        // 这条永远不存在
        //String myLinkedVlogCountsStr = redis.get(REDIS_VLOG_BE_LIKED_COUNTS + ":" + userId);
        String myLinkedVloggerLinkedMeCountsStr = redis.get(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + userId);


        Integer myFollowsCounts = 0;
        Integer myFansCounts = 0;
        Integer totalLinkedMeCounts = 0;
        Integer myLinkedVlog = 0;
        Integer myLinkedVlogger = 0;

        if (!StringUtils.isBlank(myFollowsCountsStr)){
            myFollowsCounts = Integer.valueOf(myFollowsCountsStr);
        }
        if (!StringUtils.isBlank(myFansCountsStr)){
            myFansCounts = Integer.valueOf(myFansCountsStr);
        }
//        if (!StringUtils.isBlank(myLinkedVlogCountsStr)){
//            myLinkedVlog = Integer.valueOf(myLinkedVlogCountsStr);
//        }
        if (!StringUtils.isBlank(myLinkedVloggerLinkedMeCountsStr)){
            myLinkedVlogger = Integer.valueOf(myLinkedVloggerLinkedMeCountsStr);
        }
        totalLinkedMeCounts = myLinkedVlog + myLinkedVlogger;

        usersVo.setMyFollowsCounts(myFollowsCounts);
        usersVo.setMyFansCounts(myFansCounts);
        usersVo.setTotalLikeMeCounts(totalLinkedMeCounts);

        return GraceJSONResult.ok(usersVo);
    }

    @PostMapping("modifyUserInfo")
    public GraceJSONResult modifyUserInfo(@RequestBody UpdatedUserBo updatedUserBo,
                                          @RequestParam Integer type) throws Exception{

        // 检验传过来的参数是不是我们提前定义好的修改信息
        UserInfoModifyType.checkUserInfoTypeIsRight(type);
        Users user = userService.updateUserInfo(updatedUserBo, type);
        return GraceJSONResult.ok(user);
    }

//    @PostMapping("modifyImage")
//    public GraceJSONResult modifyImage(@RequestBody UpdatedUserBo updatedUserBo,
//                                          @RequestParam Integer type) throws Exception{
//
//        // 检验传过来的参数是不是我们提前定义好的修改信息
//        UserInfoModifyType.checkUserInfoTypeIsRight(type);
//        Users user = userService.updateUserInfo(updatedUserBo, type);
//        return GraceJSONResult.ok(user);
//    }



    @PostMapping("modifyImage")
    public GraceJSONResult modifyImage(@RequestParam String userId, @RequestParam Integer type,
                                       MultipartFile file) throws Exception{

        if (type != FileTypeEnum.BGIMG.type && type != FileTypeEnum.FACE.type){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        // 得到文件名
        String filename = file.getOriginalFilename();

        MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, file.getInputStream());
        String imgUrl = minIOConfig.getFileHost()
                + "/" + minIOConfig.getBucketName()
                + "/" + filename;

        // 修改图片地址到数据库
        UpdatedUserBo updatedUserBo = new UpdatedUserBo();
        updatedUserBo.setId(userId);
        if (type == FileTypeEnum.FACE.type){
            updatedUserBo.setFace(imgUrl);
        }else {
            updatedUserBo.setBgImg(imgUrl);
        }
        Users user = userService.updateUserInfo(updatedUserBo);
        return GraceJSONResult.ok(user);
    }


}

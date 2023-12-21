package com.douyin.service;

import com.douyin.service.bo.UpdatedUserBo;
import com.douyin.service.bo.VlogBO;
import com.douyin.service.grace.PagedGridResult;
import com.douyin.service.pojo.Users;
import com.douyin.service.vo.IndexVlogVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface VlogService {
    /**
     * vlog创建
     * @param vlogBO
     */
    public void creatVlog(VlogBO vlogBO);

    /**
     * 自定义查询首页 、 或者搜索的列表
     * @param search
     */
    public PagedGridResult getIndexVlogList( String userId,String search, Integer page, Integer pageSize);

    /**
     * vlog创建
     * @param vlogId
     */
    public IndexVlogVO getVlogDetailById(String userId, String vlogId);

    /**
     * vlog创建
     * @param vlogId
     */
    public void changeToPrivateOrPublic(String userId,
                                        String vlogId,
                                        Integer YesOrNo);

    /**
     * 查询用来公开的 、 私密的视频列表
     * @param
     */
    public PagedGridResult queryMyVlog(String userId,Integer page, Integer pageSize,
                                        Integer YesOrNo);

    public void userLikeVlog(String userId,
                             String vlogId);

    public void userUnLikeVlog(String userId,
                              String vlogId);

    /**
     * 获取视频点赞总数
     * @param
     */
    public int countLikeVlog(String vlogId);

    /**
     * 查询用户用户点赞过的短视频
     * @param
     */
    public PagedGridResult getMyLikedVlogList(String userId,Integer page, Integer pageSize);

    /**
     * 查询用户关注的博主发布的短视频
     * @param
     */
    public PagedGridResult getMyFollowVlogList(String myId,Integer page, Integer pageSize);

    /**
     * 查询朋友发布的短视频
     * @param
     */
    public PagedGridResult getMyFriendVlogList(String myId,Integer page, Integer pageSize);
}

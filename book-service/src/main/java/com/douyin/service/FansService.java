package com.douyin.service;


import com.douyin.service.grace.PagedGridResult;

public interface FansService {

    /**
     * 关注博主
     * @param myId
     * @param vlogerId
     */
    public void doFollow(String myId, String vlogerId);
    public void doCancel(String myId, String vlogerId);
    public boolean queryDoIFollowVloger(String myId, String vlogerId);

    // 查询我关注的博主列表
    public PagedGridResult queryMyFollows(String myId, Integer page, Integer pageSize);
    // 查询我的粉丝列表
    public PagedGridResult queryMyFans(String myId, Integer page, Integer pageSize);
}

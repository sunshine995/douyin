package com.douyin.service;

import com.douyin.service.bo.UpdatedUserBo;
import com.douyin.service.pojo.Users;

public interface UserService {
    /**
     * 判断是否存在用户信息
     * @param mobile
     * @return
     */

    public Users queryMobileIsExist(String mobile);

    /**
     * 创建用户信息，并且返回用户对象
     * @param mobile
     * @return
     */
    public Users registerMobile(String mobile);

    /**
     * 查询用户信息，并且返回用户对象
     * @param userId
     * @return
     */
    public Users queryUser(String userId);

    /**
     * 用户信息修改
     * @param updatedUserBo
     * @return
     */
    public Users updateUserInfo(UpdatedUserBo updatedUserBo);

    /**
     * 用户信息修改
     * @param updatedUserBo
     * @return
     */
    public Users updateUserInfo(UpdatedUserBo updatedUserBo, Integer type);
}

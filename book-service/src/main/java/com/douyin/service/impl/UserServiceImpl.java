package com.douyin.service.impl;

import com.douyin.enums.Sex;
import com.douyin.enums.UserInfoModifyType;
import com.douyin.enums.YesOrNo;
import com.douyin.service.UserService;
import com.douyin.service.bo.UpdatedUserBo;
import com.douyin.service.exceptions.GraceException;
import com.douyin.service.grace.ResponseStatusEnum;
import com.douyin.service.mapper.UsersMapper;
import com.douyin.service.pojo.Users;
import com.douyin.service.utils.DateUtil;
import com.douyin.service.utils.DesensitizationUtil;
import com.douyin.service.vo.UsersVo;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;
    private static final String USER_FACE1 = "https://seopic.699pic.com/photo/30002/7499.jpg_wh1200.jpg";

    @Override
    public Users queryUser(String userId) {
        //根据用户id查询用户
        Users user = usersMapper.selectByPrimaryKey(userId);
        return user;
    }

    @Override
    public Users updateUserInfo(UpdatedUserBo updatedUserBo, Integer type) {

        // 自定义查询类型
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();

        if (type == UserInfoModifyType.NICKNAME.type){
            //criteria.andCondition("mobile", mobile);
            criteria.andEqualTo("nickname",updatedUserBo.getNickname());
            Users user = usersMapper.selectOneByExample(userExample);
            if (user != null){
                GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_NICKNAME_EXIST_ERROR);
            }

        }
        if (type == UserInfoModifyType.IMOOCNUM.type){
            //criteria.andCondition("mobile", mobile);
            criteria.andEqualTo("imoocNum",updatedUserBo.getImoocNum());
            Users user = usersMapper.selectOneByExample(userExample);
            if (user != null){
                GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_NICKNAME_EXIST_ERROR);
            }
            Users tempUser = queryUser(updatedUserBo.getId());
            if (tempUser.getCanImoocNumBeUpdated() == YesOrNo.NO.type){
                GraceException.display(ResponseStatusEnum.USER_INFO_CANT_UPDATED_IMOOCNUM_ERROR);
            }
            updatedUserBo.setCanImoocNumBeUpdated(YesOrNo.NO.type);
        }
        return updateUserInfo(updatedUserBo);
    }

    @Override
    public Users updateUserInfo(UpdatedUserBo updatedUserBo) {
        Users pendingUser = new Users();
        BeanUtils.copyProperties(updatedUserBo, pendingUser);
        int res = usersMapper.updateByPrimaryKeySelective(pendingUser);
        if (res != 1){
            GraceException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
        }
        return queryUser(updatedUserBo.getId());
    }

    @Override
    public Users queryMobileIsExist(String mobile) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();

        //criteria.andCondition("mobile", mobile);
        criteria.andEqualTo("mobile",mobile);
        Users user = usersMapper.selectOneByExample(userExample);
        // 不管是否存在都返回这么一个users对象
        return user;
    }

    @Transactional
    @Override
    public Users registerMobile(String mobile) {
        // 生成全局唯一主键
        String userId = sid.nextShort();
        Users user = new Users();
        // 然后往里面放一些属性
        user.setId(userId);

        user.setMobile(mobile);
        user.setNickname("用户：" + DesensitizationUtil.commonDisplay(mobile));
        user.setImoocNum("用户：" + DesensitizationUtil.commonDisplay(mobile));
        user.setFace(USER_FACE1);

        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        user.setSex(Sex.secret.type);

        user.setCountry("中国");
        user.setProvince("");
        user.setCity("");
        user.setDistrict("");
        user.setDescription("这家伙很懒，什么都没留下~");
        user.setCanImoocNumBeUpdated(YesOrNo.YES.type);

        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        usersMapper.insert(user);

        return user;
    }
}

package com.douyin.service.impl;

import com.douyin.base.BaseInfoProperties;
import com.douyin.enums.YesOrNo;
import com.douyin.service.FansService;
import com.douyin.service.grace.PagedGridResult;
import com.douyin.service.mapper.FansMapper;
import com.douyin.service.mapper.FansMapperCustom;
import com.douyin.service.pojo.Fans;
import com.douyin.service.vo.FansVO;
import com.douyin.service.vo.VlogerVO;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class FansServiceImpl extends BaseInfoProperties implements FansService {

    @Autowired
    private FansMapper fansMapper;

    @Autowired
    private FansMapperCustom fansMapperCustom;

    @Autowired
    private Sid sid;


    @Transactional
    // 生成一个新的关注列表
    @Override
    public void doFollow(String myId, String vlogerId) {
        String fid = sid.nextShort();

        Fans fans = new Fans();
        fans.setId(fid);
        fans.setFanId(myId);
        fans.setVlogerId(vlogerId);

        // 判断对方是否关注我，如果关注我则互为朋友关系
        Fans fans1 = queryFansRelationship(myId, vlogerId);
        if(fans1 != null){
            fans.setIsFanFriendOfMine(YesOrNo.YES.type);
            fans1.setIsFanFriendOfMine(YesOrNo.YES.type);

            fansMapper.updateByPrimaryKey(fans1);
        }else {
            fans.setIsFanFriendOfMine(YesOrNo.NO.type);
        }
        fansMapper.insert(fans);
        // 系统消息
        //msgService.createMsg(myId, vlogerId, MessageEnum.FOLLOW_YOU.type, null);
    }

    @Transactional
    @Override
    public void doCancel(String myId, String vlogerId) {
        // 首先判断我们是否是朋友关系，如果是则取消
        Fans fan = queryFansRelationship(vlogerId, myId);
        if (fan != null && fan.getIsFanFriendOfMine() == YesOrNo.YES.type){
            // 抹除双方的朋友关系，自己的关系删除即可
            Fans pendingFan = queryFansRelationship(myId, vlogerId);
            pendingFan.setIsFanFriendOfMine(YesOrNo.NO.type);
            fansMapper.updateByPrimaryKeySelective(pendingFan);
        }
        // 删除自己的关联表记录
        fansMapper.deleteByPrimaryKey(fan.getId());
        //System.out.println(delete);
    }

    @Override
    public boolean queryDoIFollowVloger(String myId, String vlogerId) {
        Fans fan = queryFansRelationship(vlogerId, myId);
        if(fan == null)return false;
        return true;

    }

    // 查询我关注的用户列表
    @Override
    public PagedGridResult queryMyFollows(String myId, Integer page, Integer pageSize) {
        // 把我构造的map new 出来，
        Map<String, Object> map = new HashMap<>();
        map.put("myId", myId);
        PageHelper.startPage(page, pageSize);

        List<VlogerVO> list = fansMapperCustom.queryMyFollows(map);
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult queryMyFans(String myId, Integer page, Integer pageSize) {

        /**
         * 判断粉丝是不是我的朋友u（互粉互关）
         * 普通做法
         * 多表关联 + 嵌套关联查询  这样会违反多表关联的规范，不可取，高并发下会出现性能问题
         *
         * 常规做法：
         * 1、避免过多表关联查询，先查询我的粉丝列表，获取fansList
         * 2、判断粉丝关注我，我也关注粉丝 -》针对我们普通做法的一个解耦（循环fansList，获取每一个粉丝，再去数据库查询我是否关注他）
         * 3、如果我也关注粉丝的话，说明我俩互为朋友关系（互关互粉，则标记 flag 为  true）
         *
         *
         * 高端做法：
         * 1、关注，取关的时候，关系保存在 redis 中，不依赖数据库
         *
         */

        Map<String, Object> map = new HashMap<>();
        map.put("myId", myId);
        PageHelper.startPage(page, pageSize);

        List<FansVO> list = fansMapperCustom.queryMyFans(map);
        for (FansVO f : list){
          String relationship = redis.get(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" +f.getFanId());
          if (StringUtils.isNotBlank(relationship) && relationship.equalsIgnoreCase("1")){
              f.setFriend(true);
          }
        }
        return setterPagedGrid(list, page);
    }

    public Fans queryFansRelationship(String fanId, String vlogerId){
        // 构建example
        Example example = new Example(Fans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("fanId", vlogerId);
        criteria.andEqualTo("vlogerId", fanId);

        List list = fansMapper.selectByExample(example);

        Fans fan = null;
        if (list != null && list.size() > 0 && !list.isEmpty()){
            fan = (Fans) list.get(0);
        }
        return fan;

    }

}

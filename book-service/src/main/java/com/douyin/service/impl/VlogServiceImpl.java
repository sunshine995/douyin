package com.douyin.service.impl;


import com.douyin.base.BaseInfoProperties;
import com.douyin.enums.YesOrNo;
import com.douyin.service.FansService;
import com.douyin.service.VlogService;
import com.douyin.service.bo.VlogBO;
import com.douyin.service.grace.PagedGridResult;
import com.douyin.service.mapper.MyLikedVlogMapper;
import com.douyin.service.mapper.VlogMapper;
import com.douyin.service.mapper.VlogMapperCustom;
import com.douyin.service.pojo.MyLikedVlog;
import com.douyin.service.pojo.Users;
import com.douyin.service.pojo.Vlog;
import com.douyin.service.vo.IndexVlogVO;
import com.github.pagehelper.PageHelper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class VlogServiceImpl extends BaseInfoProperties implements VlogService {


    // 在mapper中加入注解 @Repository 不会暴红线
    @Autowired
    private  VlogMapper vlogMapper;
    @Autowired
    private VlogMapperCustom vlogMapperCustom;

    @Autowired
    private MyLikedVlogMapper myLikedVlogMapper;

    // 注入粉丝service
    @Autowired
    private FansService fansService;

    // 这个是生成唯一主键的
    @Autowired
    private Sid sid;

    @Transactional
    @Override
    public void creatVlog(VlogBO vlogBO) {
        Vlog vlog = new Vlog();
        String vid = sid.nextShort();
        BeanUtils.copyProperties(vlogBO, vlog);

        vlog.setId(vid);
        vlog.setCommentsCounts(0);
        vlog.setLikeCounts(0);
        // 默认是公开的
        vlog.setIsPrivate(YesOrNo.NO.type);
        vlog.setCreatedTime(new Date());
        vlog.setUpdatedTime(new Date());

        vlogMapper.insert(vlog);

    }

    @Override
    public PagedGridResult getIndexVlogList(String userId,
                                            String search,
                                            Integer page,
                                            Integer pageSize) {

        // 分页助手，实现自定义分页
        PageHelper.startPage(page,pageSize);

        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(search)){
            map.put("search", search);
        }
        List<IndexVlogVO> list = vlogMapperCustom.getIndexVlogList(map);
        for (IndexVlogVO v : list){
            String vlogerId = v.getVlogerId(); // 这个是视频博主的id
            String vlogId = v.getVlogId();
            if (StringUtils.isNotBlank(userId)){
                v.setDoIFollowVloger(fansService.queryDoIFollowVloger(userId, vlogerId));
                v.setDoILikeThisVlog(isBeLike(userId, vlogId));
            }
            v.setLikeCounts(countLikeVlog(vlogId));

        }
      return setterPagedGrid(list,page);
    }

    // 判断一个视频是否是被点赞过的
    private boolean isBeLike(String myId, String vlogId){
        String isLike = redis.get(REDIS_USER_LIKE_VLOG + ":" + myId + ":" + vlogId);
        if(StringUtils.isNotBlank(isLike) && isLike.equalsIgnoreCase("1")){
            return true;
        }
        return false;
    }

    // 查询视频点赞总数
    @Override
    public int countLikeVlog(String vlogId){
        String countLike = redis.get(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId);
        if (StringUtils.isNotBlank(countLike)){
            //System.out.println(countLike);
            return Integer.valueOf(countLike);
        }
        return 0;
    }


    @Override
    public IndexVlogVO getVlogDetailById(String userId,String vlogId) {
        Map<String, Object> map = new HashMap<>();
        map.put("vlogId", vlogId);
        List<IndexVlogVO> list = vlogMapperCustom.getVlogDetailById(map);
        if (list != null && list.size() > 0 && !list.isEmpty()){
            return setterVlog(list.get(0), userId);
        }
        return null;
    }

    @Transactional
    @Override
    public void changeToPrivateOrPublic(String userId, String vlogId, Integer YesOrNo) {
        Example example = new Example(Vlog.class);
        // 创造条件，是用来匹配用户id和vlog id的
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", vlogId);
        criteria.andEqualTo("vlogerId", userId);

        Vlog pengingVlog = new Vlog();
        pengingVlog.setIsPrivate(YesOrNo);

        // elective是空属性不会被修改
        vlogMapper.updateByExampleSelective(pengingVlog, example);
    }

    @Override
    public PagedGridResult queryMyVlog(String userId, Integer page, Integer pageSize, Integer YesOrNo) {
        Example example = new Example(Vlog.class);
        // 创造条件，是用来匹配用户id和vlog id的
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("vlogerId", userId);
        criteria.andEqualTo("isPrivate", YesOrNo);
        // 分页助手，实现自定义分页
        PageHelper.startPage(page, pageSize);
        List<Vlog> vlogs = vlogMapper.selectByExample(example);
        return setterPagedGrid(vlogs, page);
    }

    // 声明式事务管理建立在AOP之上的。其本质是对方法前后进行拦截，然后在目标方法开始之前创建或者加入一个事务，在执行完目标方法之后根据执行情况提交或者回滚事务。
    @Transactional
    @Override
    public void userLikeVlog(String userId, String vlogId) {

        String rid = sid.nextShort();
        MyLikedVlog myLikedVlog =  new MyLikedVlog();
        myLikedVlog.setId(rid);
        myLikedVlog.setUserId(userId);
        myLikedVlog.setVlogId(vlogId);
        myLikedVlogMapper.insert(myLikedVlog);
    }

    @Transactional
    @Override
    public void userUnLikeVlog(String userId, String vlogId) {

        MyLikedVlog myLikedVlog =  new MyLikedVlog();
        myLikedVlog.setUserId(userId);
        myLikedVlog.setVlogId(vlogId);
        // 根据条件做一个删除
        myLikedVlogMapper.delete(myLikedVlog);
;    }

    @Override
    public PagedGridResult getMyLikedVlogList(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Map<String,Object> map = new HashMap<>();
        map.put("userId", userId);
        List<IndexVlogVO> list = vlogMapperCustom.getMyLikedVlogList(map);
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult getMyFollowVlogList(String myId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Map<String,Object> map = new HashMap<>();
        map.put("myId", myId);
        List<IndexVlogVO> list = vlogMapperCustom.getMyFollowVlogList(map);

        for (IndexVlogVO v : list){
//            //String myId = v.getVlogerId(); // 这个是视频博主的id
//            String vlogId = v.getVlogId();
//            if (StringUtils.isNotBlank(myId)){
//
//                // 将我关注的设置为 true(用户必定关注该博主)
//                v.setDoIFollowVloger(true);
//                v.setDoILikeThisVlog(isBeLike(myId, vlogId));
//            }
//            v.setLikeCounts(countLikeVlog(vlogId));
            setterVlog(v,myId);

        }
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult getMyFriendVlogList(String myId, Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);
        Map<String,Object> map = new HashMap<>();
        map.put("myId", myId);
        List<IndexVlogVO> list = vlogMapperCustom.getMyFriendVlogList(map);

        for (IndexVlogVO v : list){
//            String vlogId = v.getVlogId();
//            if (StringUtils.isNotBlank(myId)){
//
//                // 将我关注的设置为 true(用户必定关注该博主)
//                v.setDoIFollowVloger(true);
//                v.setDoILikeThisVlog(isBeLike(myId, vlogId));
//            }
//            v.setLikeCounts(countLikeVlog(vlogId));
            setterVlog(v,myId);

        }
        return setterPagedGrid(list, page);
    }

    private IndexVlogVO setterVlog(IndexVlogVO v, String userId){
        String vlogId = v.getVlogId();
        if (StringUtils.isNotBlank(userId)){

            // 将我关注的设置为 true(用户必定关注该博主)
            v.setDoIFollowVloger(true);
            v.setDoILikeThisVlog(isBeLike(userId, vlogId));
        }
        v.setLikeCounts(countLikeVlog(vlogId));
        return v;
    }
}

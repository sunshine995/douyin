package com.douyin.service.impl;

import com.douyin.base.BaseInfoProperties;
import com.douyin.enums.YesOrNo;
import com.douyin.service.CommenService;
import com.douyin.service.bo.CommentBO;
import com.douyin.service.grace.PagedGridResult;
import com.douyin.service.mapper.CommentMapper;
import com.douyin.service.mapper.CommentMapperCustom;
import com.douyin.service.pojo.Comment;
import com.douyin.service.vo.CommentVO;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl extends BaseInfoProperties implements CommenService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentMapperCustom commentMapperCustom;

    @Autowired
    private Sid sid;

    @Override
    public CommentVO createComment(CommentBO commentBO) {
        String commentId = sid.nextShort();
        // 首先生成一个 comment类
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setCommentUserId(commentBO.getCommentUserId());

        comment.setVlogerId(commentBO.getVlogerId());
        comment.setVlogId(commentBO.getVlogId());

        comment.setFatherCommentId(commentBO.getFatherCommentId());
        comment.setContent(commentBO.getContent());

        comment.setLikeCounts(0); // 默认是 0
        comment.setCreateTime(new Date());

        commentMapper.insert(comment);

        // redis操作放在Service中， 视频评论总数加一
        redis.increment(REDIS_VLOG_COMMENT_COUNTS + ":" + commentBO.getVlogId(), 1);

        // 留言后的最新评论要返回给前端进行展示
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(comment, commentVO);

        return commentVO;
    }

    @Override
    public PagedGridResult queryVlogComments(String vlogId, String userId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("vlogId", vlogId);
        PageHelper.startPage(page, pageSize);

        List<CommentVO> commentList = commentMapperCustom.getCommentList(map);
        for (CommentVO cvo : commentList){
            String commentId = cvo.getCommentId();
            // 当前短视频某个评论的点赞总数
            String countStr = redis.getHashValue(REDIS_VLOG_COMMENT_LIKED_COUNTS, commentId);
            int counts = 0;
            if(StringUtils.isNotBlank(countStr)){
                counts = Integer.valueOf(countStr);
            }
            cvo.setLikeCounts(counts);
            // 判断当前用户是否点在过该评论
            String doLike = redis.hget(REDIS_USER_COMMENT_LIKED, userId + ":" + commentId);

            if (StringUtils.isNotBlank(doLike) && doLike.equalsIgnoreCase("1")){
                cvo.setIsLike(YesOrNo.YES.type);
            }
        }

        return setterPagedGrid(commentList, page);
    }

    // 删除评论
    @Override
    public void delete(String commentId, String commentUserId, String vlogId) {
        // 条件就是对应的属性
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setCommentUserId(commentUserId);
        //comment.setVlogId(vlogId);
        commentMapper.delete(comment);

        // 评论总数 -1
        redis.decrement(REDIS_VLOG_COMMENT_COUNTS + ":" + vlogId, 1);
    }
}

package com.douyin.service;

import com.douyin.service.bo.CommentBO;
import com.douyin.service.grace.PagedGridResult;
import com.douyin.service.vo.CommentVO;

public interface CommenService {

    /**
     * 发表评论
     * @param commentBO
     */
    public CommentVO createComment(CommentBO commentBO);

    public PagedGridResult queryVlogComments(String vlogId,
                                             String userId,
                                             Integer page,
                                             Integer pageSize);

    /**
     * 删除评论
     * @param commentId
     * @param commentUserId
     * @param vlogId
     */
    public void delete(String commentId, String commentUserId, String vlogId);

   // public void delete(String commentId, String commentUserId, String vlogId);
}

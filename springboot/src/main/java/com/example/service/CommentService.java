package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Comment;
import com.example.mapper.CommentMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.security.auth.login.CredentialException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 功能：博客评论 业务类
 * 作者：captain_dong
 * 日期：2024/1/27 1:12
 */
@Service
public class CommentService {
    @Resource
    private CommentMapper commentMapper;

    /**
     * 添加评论
     * @param comment
     */
    public void add(Comment comment) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())){
            comment.setUserId(currentUser.getId());
        }
        comment.setTime(DateUtil.now());
        commentMapper.insert(comment);  //先插入数据  拿到主键ID 再设置数据
        if (comment.getRootId() == null){
            comment.setRootId(comment.getId());
            commentMapper.updateById(comment);  //注意：  更新一下 root_id
        }
    }

    /**
     * 删除评论
     * @param id
     */
    public void deleteById(Integer id) {
        commentMapper.deleteById(id);
    }

    /**
     *  批量删除评论
     * @param ids
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            commentMapper.deleteById(id);
        }
    }

    /**
     * 更新评论
     * @param comment
     */
    public void updateById(Comment comment) {
        commentMapper.updateById(comment);
    }

    /**
     * 根据ID查询
     */
    public Comment selectById(Integer id) {
        return commentMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Comment> selectAll(Comment comment) {
        return commentMapper.selectAll(comment);
    }

    public List<Comment> selectForUser(Comment comment){
        List<Comment> commentList = commentMapper.selectForUser(comment);
        for (Comment c : commentList) {  //查询回复列表
            Comment param = new Comment();
            param.setRootId(c.getId());
            List<Comment> children=this.selectAll(param);
            children=children.stream().filter(child->!child.getId().equals(c.getId())).collect(Collectors.toList());  //排除当前查询结果里最外层节点

            c.setChildren(children);
        }
        return commentList;
    }

    /**
     * 分页查询
     */
    public PageInfo<Comment> selectPage(Comment comment, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> list = commentMapper.selectAll(comment);
        return PageInfo.of(list);
    }

    /**
     * 查询评论个数
     * @param fid
     * @param module
     * @return
     */
    public Integer selectCount(Integer fid, String module) {
        return commentMapper.selectCount(fid,module);
    }
}
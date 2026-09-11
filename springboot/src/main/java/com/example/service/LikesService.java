package com.example.service;

import com.example.entity.Account;
import com.example.entity.Likes;
import com.example.mapper.LikesMapper;
import com.example.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 功能：点赞业务开发类
 * 作者：captain_dong
 * 日期：2024/1/26 17:44
 */
@Service
public class LikesService {
    @Resource
    LikesMapper likesMapper;

    /**
     * 点赞或取消点赞
     * @param likes
     */
    public void set(Likes likes) {
        Account currentUser = TokenUtils.getCurrentUser();
        likes.setUserId(currentUser.getId());
        Likes dblikes= likesMapper.selectUserLikes(likes);
        if (dblikes==null){
            likesMapper.insert(likes);
        }else {
            likesMapper.deleteById(dblikes.getId());
        }

    }

    /**
     * 查询当前用户点赞信息（是否点过赞）
     * @param fid
     * @param module
     * @return
     */
    public Likes selectUserLikes(Integer fid,String module){
        Account currentUser = TokenUtils.getCurrentUser();
        Likes likes=new Likes();
        likes.setUserId(currentUser.getId());
        likes.setFid(fid);
        likes.setModule(module);
        return likesMapper.selectUserLikes(likes);
    }

    public int selectByFidAndModule(Integer fid, String module) {
        return likesMapper.selectByFidAndModule(fid, module);
    }
}
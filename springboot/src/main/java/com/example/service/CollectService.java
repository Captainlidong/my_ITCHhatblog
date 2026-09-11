package com.example.service;

import com.example.entity.Account;
import com.example.entity.Collect;
import com.example.mapper.CollectMapper;
import com.example.utils.TokenUtils;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 功能：收藏功能业务 开发
 * 作者：captain_dong
 * 日期：2024/1/26 20:52
 */
@Service
public class CollectService {
    @Resource
    CollectMapper collectMapper;

    public void set(Collect collect) {
        Account currentUser = TokenUtils.getCurrentUser();
        collect.setUserId(currentUser.getId());
        Collect dbCollect = collectMapper.selectUserCollect(collect);
        if (dbCollect==null){
            collectMapper.insert(collect);
        }else {
            collectMapper.deleteById(dbCollect.getId());
        }
    }

    /**
     * 查询当前用户是否收藏过
     * @param fid
     * @param module
     * @return
     */
    public Collect selectUserCollect(Integer fid,String module){
        Account currentUser = TokenUtils.getCurrentUser();
        Collect collect = new Collect();
        collect.setUserId(currentUser.getId());
        collect.setFid(fid);
        collect.setModule(module);
        return collectMapper.selectUserCollect(collect);
    }

    public int selectByFidAndModule(Integer fid,String module){
        return collectMapper.selectByFidAndModule(fid,module);
    }
}
package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.db.DbUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.*;
import java.util.List;

/**
 * 功能：
 * 作者：captain_dong
 * 日期：2023/12/19 21:46
 */
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public void register(Account account) {
        User user=new User();
        BeanUtils.copyProperties(account,user);
        this.add(user);
    }

    /**
     * 新增用户
     * @param user
     */
    public void add(User user){
        //业务方法
        //1.判断用户账号是否重复
        User dbUser = userMapper.selectByUserName(user.getUsername());
        if (dbUser!= null){
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        //2.判断用户密码是否为空
        if (ObjectUtil.isEmpty(user.getPassword())){
            user.setPassword(Constants.USER_DEFAULT_PASSWORD); //默认密码：123
        }
        //3.判断用户名称是否为空
        if (ObjectUtil.isEmpty(user.getName())){
            user.setName(user.getUsername());
        }
        //4.默认用户角色
        user.setRole(RoleEnum.USER.name());

        userMapper.insert(user);
    }

    /**
     * 删除
     * @param id
     */
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    /**
     * 批量删除
     * @param ids
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    /**
     * 更新用户信息
     * @param user
     */
    public void updateById(User user) {
        userMapper.updateById(user);
    }

    /**
     * 根据用户ID查询用户信息
     * @param id
     * @return
     */
    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

    /**
     * 查询所有用户
     * @param user
     * @return
     */
    public List<User> selectAll(User user) {
        return userMapper.selectAll(user);
    }

    /**
     * 分页查询用户
     * @param user
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<User> selectPage(User user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<User> users = userMapper.selectAll(user);
        return PageInfo.of(users);
        //return new PageInfo<>(userMapper.selectAll(user));
    }

    /**
     * 用户登录逻辑
     * @param account
     * @return
     */
    public Account login(Account account) {
        Account dbUser = userMapper.selectByUserName(account.getUsername());
        //用户是否为空
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        //密码比对
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbUser.getId() + "-" + RoleEnum.USER.name();
        String token = TokenUtils.createToken(tokenData, dbUser.getPassword());
        dbUser.setToken(token);
        return dbUser;
    }

    /**
     * 用户修改密码
     * @param account
     */
    public void updatePassword(Account account) {
        User bdUser = userMapper.selectByUserName(account.getUsername());
        if (ObjectUtil.isNull(bdUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(bdUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        bdUser.setPassword(account.getNewPassword());
        userMapper.updateById(bdUser);
    }
}
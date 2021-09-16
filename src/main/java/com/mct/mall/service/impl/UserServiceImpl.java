package com.mct.mall.service.impl;

import com.mct.mall.exception.MallException;
import com.mct.mall.exception.MallExceptionEnum;
import com.mct.mall.model.dao.UserMapper;
import com.mct.mall.model.pojo.User;
import com.mct.mall.service.UserService;
import com.mct.mall.util.MD5Utils;
import java.security.NoSuchAlgorithmException;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/8/8 7:09 下午
 * @version:v1.0
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    UserMapper userMapper;

    @Override
    public User getUser(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public void register(String userName, String password) throws MallException, NoSuchAlgorithmException {
        // 查询用户名是否已经存在
        User result = userMapper.selectByName(userName);
        if (result != null) {
            throw new MallException(MallExceptionEnum.NAME_EXISTED);
        }
        User user = new User();
        user.setUsername(userName);
        user.setPassword(MD5Utils.getMD5Str(password));

        int cnt = userMapper.insertSelective(user);

        if (cnt == 0) {
            throw new MallException(MallExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public User login(String userName, String password) throws MallException {
        String md5Password = null;
        try {
            md5Password = MD5Utils.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        User user = userMapper.selectLogin(userName, md5Password);
        if (user == null) {
            throw new MallException(MallExceptionEnum.WONG_PASSWORD);
        }
        return user;
    }

    @Override
    public void updateInformation(User user) throws MallException {
        int num = userMapper.updateByPrimaryKeySelective(user);
        if (num > 1) {
            throw  new MallException(MallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public boolean checkAdminRole(User user) {
        return user.getRole().equals(2);
    }
}

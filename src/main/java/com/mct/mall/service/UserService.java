package com.mct.mall.service;

import com.mct.mall.exception.MallException;
import com.mct.mall.model.pojo.User;
import java.security.NoSuchAlgorithmException;

/**
 * @description: TODO
 * @author: zhengran
 * @modified By: zhengran
 * @date: Created in 2021/8/8 7:08 下午
 * @version:v1.0
 */
public interface UserService {
    User getUser(Integer id);

    void register(String userName, String password) throws MallException, NoSuchAlgorithmException;

    User login(String userName, String password) throws MallException;

    void updateInformation(User user) throws MallException;

    boolean checkAdminRole(User user);
}

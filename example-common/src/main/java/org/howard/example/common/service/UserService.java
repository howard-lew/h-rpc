package org.howard.example.common.service;

import org.howard.example.common.model.User;

/**
 * 用户服务
 *
 * @Author HowardLiu
 *
 */
public interface UserService {
    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    User getUser(User user);
}

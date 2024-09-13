package org.howard.example.provider;

import org.howard.example.common.model.User;
import org.howard.example.common.service.UserService;

/**
 * @Author HowardLiu
 * 
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名: " + user.getName());
        return user;
    }
}

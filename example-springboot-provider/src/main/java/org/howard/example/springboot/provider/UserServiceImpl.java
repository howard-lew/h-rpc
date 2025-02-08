package org.howard.example.springboot.provider;

import org.howard.example.common.model.User;
import org.howard.example.common.service.UserService;
import org.howard.hrpc.spring.boot.starter.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 *
 * @Author <a href="https://github.com/howard12358">HowardLiu</a>
 */
@Service
@RpcService
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}

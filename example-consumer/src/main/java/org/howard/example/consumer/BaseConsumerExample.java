package org.howard.example.consumer;

import org.howard.example.common.model.User;
import org.howard.example.common.service.UserService;
import org.howard.hrpc.proxy.ServiceProxyFactory;

/**
 * @Author HowardLiu
 */
public class BaseConsumerExample {
    public static void main(String[] args) {
        User user = new User();
        user.setName("howard");
        // 调用服务提供者 UserService
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User u = userService.getUser(user);
        System.out.println(u);
    }
}

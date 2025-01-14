package org.howard.example.springboot.consumer;

import org.howard.example.common.model.User;
import org.howard.example.common.service.UserService;
import org.howard.hrpc.spring.boot.starter.annotation.RpcReference;
import org.springframework.stereotype.Service;

/**
 * 示例服务实现类
 *
 * @Author <a href="https://github.com/yige-howard">HowardLiu</a>
 */
@Service
public class ExampleServiceImpl {

    /**
     * 使用 Rpc 框架注入
     */
    @RpcReference
    private UserService userService;

    /**
     * 测试方法
     */
    public void test() {
        User user = new User();
        user.setName("howard");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }

}

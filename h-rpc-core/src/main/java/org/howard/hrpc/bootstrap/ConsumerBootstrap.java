package org.howard.hrpc.bootstrap;

import org.howard.hrpc.RpcApplication;

/**
 * 服务消费者启动类（初始化）
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
public class ConsumerBootstrap {
    /**
     * 初始化
     */
    public static void init() {
        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();
    }
}

package org.howard.example.provider;

import org.howard.example.common.service.UserService;
import org.howard.hrpc.RpcApplication;
import org.howard.hrpc.config.RpcConfig;
import org.howard.hrpc.registry.LocalRegistry;
import org.howard.hrpc.server.VertxHttpServer;

/**
 * 服务提供者示例
 *
 * @Author HowardLiu
 */
public class BaseProviderExample {
    public static void main(String[] args) {
        RpcApplication.init();
        RpcConfig config = RpcApplication.getRpcConfig();

        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(config.getPort());
    }
}

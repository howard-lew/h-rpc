package org.howard.example.provider;

import org.howard.example.common.service.UserService;
import org.howard.example.provider.impl.UserServiceImpl;
import org.howard.hrpc.RpcApplication;
import org.howard.hrpc.config.RegistryConfig;
import org.howard.hrpc.config.RpcConfig;
import org.howard.hrpc.model.ServiceMetaInfo;
import org.howard.hrpc.registry.LocalRegistry;
import org.howard.hrpc.registry.Registry;
import org.howard.hrpc.registry.RegistryFactory;
import org.howard.hrpc.server.VertxHttpServer;

/**
 * 服务提供者示例
 *
 * @Author HowardLiu
 */
public class ProviderExample {
    public static void main(String[] args) {
        RpcApplication.init();
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());
        try {
            registry.register(new ServiceMetaInfo(){{
                setServiceName(serviceName);
                setServiceHost(rpcConfig.getServerHost());
                setServicePort(rpcConfig.getPort());
            }});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(rpcConfig.getPort());
    }
}

package org.howard.example.provider;

import org.howard.example.common.service.UserService;
import org.howard.example.provider.impl.UserServiceImpl;
import org.howard.hrpc.bootstrap.ProviderBootstrap;
import org.howard.hrpc.model.ServiceRegisterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供者示例
 *
 * @Author HowardLiu
 */
public class ProviderExample {
    public static void main(String[] args) {
        // 要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // 服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList);

//        RpcApplication.init();
//        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
//
//        String serviceName = UserService.class.getName();
//        LocalRegistry.register(serviceName, UserServiceImpl.class);
//
//        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
//        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());
//        try {
//            registry.register(new ServiceMetaInfo(){{
//                setServiceName(serviceName);
//                setServiceHost(rpcConfig.getServerHost());
//                setServicePort(rpcConfig.getPort());
//            }});
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        VertxHttpServer vertxHttpServer = new VertxHttpServer();
//        vertxHttpServer.doStart(rpcConfig.getPort());
    }
}

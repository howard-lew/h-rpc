package org.howard.hrpc.bootstrap;

import org.howard.hrpc.RpcApplication;
import org.howard.hrpc.config.RpcConfig;
import org.howard.hrpc.model.ServiceMetaInfo;
import org.howard.hrpc.model.ServiceRegisterInfo;
import org.howard.hrpc.registry.LocalRegistry;
import org.howard.hrpc.registry.Registry;
import org.howard.hrpc.registry.RegistryFactory;
import org.howard.hrpc.server.VertxHttpServer;

import java.util.List;

/**
 * 服务提供者启动类（初始化）
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
public class ProviderBootstrap {
    /**
     * 初始化
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();
        // 全局配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 服务注册
        serviceRegisterInfoList.forEach(serviceRegisterInfo -> {
            String serviceName = serviceRegisterInfo.getServiceName();
            // 本地注册
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            // 注册到注册中心
            Registry registry = RegistryFactory.getRegistry(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo() {{
                setServiceName(serviceName);
                setServiceHost(rpcConfig.getServerHost());
                setServicePort(rpcConfig.getPort());
            }};
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " 服务注册失败", e);
            }
        });

        // 启动服务器
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(rpcConfig.getPort());
    }
}

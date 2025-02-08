package org.howard.hrpc.spring.boot.starter.bootstrap;

import org.howard.hrpc.RpcApplication;
import org.howard.hrpc.config.RpcConfig;
import org.howard.hrpc.model.ServiceMetaInfo;
import org.howard.hrpc.registry.LocalRegistry;
import org.howard.hrpc.registry.Registry;
import org.howard.hrpc.registry.RegistryFactory;
import org.howard.hrpc.spring.boot.starter.annotation.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Rpc 服务提供者启动
 *
 * @Author <a href="https://github.com/howard12358">HowardLiu</a>
 */
public class RpcProviderBootstrap implements BeanPostProcessor {
    /**
     * Bean 初始化后执行，注册服务
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 需要注册服务
            // 1. 获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();
            // 2. 注册服务
            // 本地注册
            LocalRegistry.register(serviceName, beanClass);

            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

            // 注册服务到注册中心
            Registry registry = RegistryFactory.getRegistry(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo() {{
                setServiceName(serviceName);
                setServiceVersion(serviceVersion);
                setServiceHost(rpcConfig.getServerHost());
                setServicePort(rpcConfig.getPort());
            }};
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " 服务注册失败", e);
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}

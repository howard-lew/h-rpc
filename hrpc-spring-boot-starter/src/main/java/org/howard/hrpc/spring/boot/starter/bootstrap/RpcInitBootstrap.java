package org.howard.hrpc.spring.boot.starter.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.howard.hrpc.RpcApplication;
import org.howard.hrpc.config.RpcConfig;
import org.howard.hrpc.server.VertxHttpServer;
import org.howard.hrpc.spring.boot.starter.annotation.EnableRpc;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Rpc 框架启动
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取 EnableRpc 注解的属性值
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                .get("needServer");

        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();

        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 启动服务器
        if (needServer) {
            VertxHttpServer vertxHttpServer = new VertxHttpServer();
            vertxHttpServer.doStart(rpcConfig.getPort());
        } else {
            log.info("不启动 server");
        }
    }
}

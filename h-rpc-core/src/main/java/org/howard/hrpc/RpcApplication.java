package org.howard.hrpc;

import lombok.extern.slf4j.Slf4j;
import org.howard.hrpc.config.RegistryConfig;
import org.howard.hrpc.config.RpcConfig;
import org.howard.hrpc.registry.Registry;
import org.howard.hrpc.registry.RegistryFactory;
import org.howard.hrpc.utils.ConfigUtils;

import static org.howard.hrpc.constant.RpcConstant.DEFAULT_CONFIG_PREFIX;

/**
 * RPC 框架应用，存放项目全局用到的变量
 *
 * @Author HowardLiu
 */
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    /**
     * 初始化配置
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 使用默认配置
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("hrpc init, config = {}", rpcConfig.toString());

        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);
    }

    /**
     * 获取配置 (采用双重检查锁单例模式实现)
     *
     * @return RpcConfig
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }

}

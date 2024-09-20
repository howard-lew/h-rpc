package org.howard.hrpc.registry;

import org.howard.hrpc.config.RegistryConfig;
import org.howard.hrpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
public interface Registry {
    /**
     * 初始化
     *
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务，服务端
     *
     * @param serviceMetaInfo
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务，服务端
     *
     * @param serviceMetaInfo
     */
    void unregister(ServiceMetaInfo serviceMetaInfo)throws Exception;

    /**
     * 服务发现（获取服务节点列表），消费端
     *
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 服务销毁
     */
    void destroy();
}

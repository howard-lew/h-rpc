package org.howard.hrpc.registry;

import org.howard.hrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心服务的本地缓存
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
public class RegistryServiceCache {
    /**
     * 服务缓存，缓存多个服务
     */
    Map<String, List<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

    /**
     * 写入缓存
     *
     * @param serviceKey          服务键名
     * @param serviceMetaInfoList 更新后的缓存列表
     */
    void writeCache(String serviceKey, List<ServiceMetaInfo> serviceMetaInfoList) {
        this.serviceCache.put(serviceKey, serviceMetaInfoList);
    }

    /**
     * 读取缓存
     *
     * @param serviceKey 服务键名
     * @return
     */
    List<ServiceMetaInfo> readCache(String serviceKey) {
        return this.serviceCache.get(serviceKey);
    }

    /**
     * 删除缓存
     *
     * @param serviceKey 服务键名
     */
    void remove(String serviceKey) {
        this.serviceCache.remove(serviceKey);
    }
}

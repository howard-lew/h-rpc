package org.howard.hrpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author HowardLiu
 * 
 */
public class LocalRegistry {
    /**
     * 服务注册表
     */
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();

    /**
     * 注册服务
     *
     * @param serviceName  服务名称
     * @param serviceImplClass 服务类
     */
    public static void register(String serviceName, Class<?> serviceImplClass) {
        map.put(serviceName, serviceImplClass);
    }

    /**
     * 获取服务
     *
     * @param serviceName 服务名称
     * @return 服务类
     */
    public static Class<?> get(String serviceName) {
        return map.get(serviceName);
    }

    /**
     * 移除服务
     *
     * @param serviceName 服务名称
     */
    public static void remove(String serviceName) {
        map.remove(serviceName);
    }
}

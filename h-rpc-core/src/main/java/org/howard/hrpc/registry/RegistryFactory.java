package org.howard.hrpc.registry;

import org.howard.hrpc.spi.SpiLoader;

/**
 * 注册中心工厂（用于创建注册中心实例）
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
public class RegistryFactory {
    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 获取注册中心实例
     *
     * @param registryKey
     * @return
     */
    public static Registry getRegistry(String registryKey) {
        return SpiLoader.getInstance(Registry.class, registryKey);
    }
}

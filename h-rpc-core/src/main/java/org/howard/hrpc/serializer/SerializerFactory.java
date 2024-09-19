package org.howard.hrpc.serializer;

import org.howard.hrpc.spi.SpiLoader;

/**
 * 序列化器工厂（工厂模式，用于获取序列化器对象）
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
public class SerializerFactory {
    static {
        // 加载所有 Serializer 实现类的 Class 对象
        SpiLoader.load(Serializer.class);
    }

    /**
     * 获取序列化器，比如 jdk -> JdkSerializer
     *
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }
}

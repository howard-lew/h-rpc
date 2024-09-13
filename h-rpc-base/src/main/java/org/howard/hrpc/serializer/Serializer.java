package org.howard.hrpc.serializer;

import java.io.IOException;

/**
 * 序列化器接口
 *
 * @Author HowardLiu
 * @Date 2024/9/12
 */
public interface Serializer {
    /**
     * 序列化
     *
     * @param obj 对象
     * @param <T> 对象类型
     * @return byte[]
     * @throws IOException
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化
     * @param data 数据
     * @param type 类型
     * @return
     * @param <T>
     * @throws IOException
     */
    <T> T deserialize(byte[] data, Class<T> type) throws IOException;
}

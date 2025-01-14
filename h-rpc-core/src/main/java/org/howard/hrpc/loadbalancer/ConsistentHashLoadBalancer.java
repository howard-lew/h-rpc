package org.howard.hrpc.loadbalancer;

import org.howard.hrpc.model.ServiceMetaInfo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器
 *
 * @Author <a href="https://github.com/yige-howard">HowardLiu</a>
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {
    /**
     * 一致性 Hash 环，存放虚拟节点
     */
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数量
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }

        // 构建虚拟节点（在每次调用负载均衡器时都重新构造 Hash 环）
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int virtualNodeKey = getSha256Hash(serviceMetaInfo.getServiceNodeKey() + "#" + i);
                virtualNodes.put(virtualNodeKey, serviceMetaInfo);
            }
        }

        int hash = getSha256Hash(requestParams);
        // 选择最接近且大于等于调用请求 hash 值的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) {
            entry = virtualNodes.firstEntry();
        }

        return entry.getValue();
    }

    /**
     * 计算对象的哈希值（SHA-256）
     *
     * @param obj
     * @return
     */
    public static int getSha256Hash(Object obj) {
        try {
            // 将对象转换为字符串
            String str = obj.toString();
            // 计算 SHA-256 哈希
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(str.getBytes(StandardCharsets.UTF_8));
            // 将前四个字节转换为 int
            return ((hashBytes[0] & 0xFF) << 24)
                    | ((hashBytes[1] & 0xFF) << 16)
                    | ((hashBytes[2] & 0xFF) << 8)
                    | (hashBytes[3] & 0xFF);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取 Hash 值
     *
     * @param key
     * @return
     */
    private int getHash(Object key) {
        return key.hashCode();
    }
}

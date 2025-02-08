package org.howard.hrpc.loadbalancer;

import org.howard.hrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡器
 *
 * @Author <a href="https://github.com/howard12358">HowardLiu</a>
 */
public class RoundRobinLoadBalancer implements LoadBalancer {
    /**
     * 当前轮询的下标
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        int size = serviceMetaInfoList.size();
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }
        // 取模轮询
        return serviceMetaInfoList.get(currentIndex.getAndIncrement() % size);
    }
}

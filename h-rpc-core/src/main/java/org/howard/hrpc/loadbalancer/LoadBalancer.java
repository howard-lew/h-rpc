package org.howard.hrpc.loadbalancer;

import org.howard.hrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡器（消费端使用）
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
public interface LoadBalancer {
    /**
     * 负载均衡算法
     *
     * @param requestParams       请求参数
     * @param serviceMetaInfoList 服务列表
     * @return
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}

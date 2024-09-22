package org.howard.hrpc.loadbalancer;

/**
 * 负载均衡算法键名常量
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
public interface LoadBalancerKeys {
    /**
     * 轮询
     */
    String ROUND_ROBIN = "roundRobin";
    /**
     * 随机
     */
    String RANDOM = "random";
    /**
     * 一致性哈希
     */
    String CONSISTENT_HASH = "consistentHash";
}

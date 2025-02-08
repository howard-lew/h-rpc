package org.howard.hrpc.loadbalancer;

import org.howard.hrpc.spi.SpiLoader;

/**
 * 负载均衡工厂
 *
 * @Author <a href="https://github.com/howard12358">HowardLiu</a>
 */
public class LoadBalancerFactory {
    static {
        SpiLoader.load(LoadBalancer.class);
    }

    /**
     * 获取负载均衡器
     *
     * @param loadBalancerKey 见 {@link LoadBalancerKeys}
     * @return
     */
    public static LoadBalancer getLoadBalancer(String loadBalancerKey) {
        return SpiLoader.getInstance(LoadBalancer.class, loadBalancerKey);
    }
}

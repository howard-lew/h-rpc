package org.howard.hrpc.fault.retry;

import org.howard.hrpc.spi.SpiLoader;

/**
 * 重试策略工厂
 *
 * @Author <a href="https://github.com/yige-howard">HowardLiu</a>
 */
public class RetryStrategyFactory {
    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 获取实例
     *
     * @param retryStrategyKey 见 {@link RetryStrategyKeys}
     * @return
     */
    public static RetryStrategy getInstance(String retryStrategyKey) {
        return SpiLoader.getInstance(RetryStrategy.class, retryStrategyKey);
    }
}

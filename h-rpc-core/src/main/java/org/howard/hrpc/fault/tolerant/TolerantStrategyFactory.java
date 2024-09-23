package org.howard.hrpc.fault.tolerant;

import org.howard.hrpc.spi.SpiLoader;

/**
 * 容错策略工厂
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
public class TolerantStrategyFactory {
    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 获取实例
     *
     * @param tolerantStrategyKey
     * @return
     */
    public static TolerantStrategy getInstance(String tolerantStrategyKey) {
        return SpiLoader.getInstance(TolerantStrategy.class, tolerantStrategyKey);
    }
}

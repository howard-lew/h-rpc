package org.howard.hrpc.fault.tolerant;

/**
 * 容错策略键
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
public interface TolerantStrategyKeys {
    /**
     * 故障恢复
     */
    String FAIL_BACK = "failBack";

    /**
     * 快速失败
     */
    String FAIL_FAST = "failFast";
}

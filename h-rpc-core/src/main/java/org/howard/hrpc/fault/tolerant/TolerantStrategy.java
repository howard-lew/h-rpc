package org.howard.hrpc.fault.tolerant;

import java.util.Map;

/**
 * 容错策略
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
public interface TolerantStrategy {
    /**
     * 容错
     *
     * @param context 上下文，用于传递数据
     * @param e       异常
     * @return
     */
    <T> T doTolerant(Map<String, Object> context, Exception e);
}

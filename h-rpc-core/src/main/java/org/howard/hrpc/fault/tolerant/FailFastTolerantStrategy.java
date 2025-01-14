package org.howard.hrpc.fault.tolerant;

import java.util.Map;

/**
 * 快速失败容错策略
 *
 * @Author <a href="https://github.com/yige-howard">HowardLiu</a>
 */
public class FailFastTolerantStrategy implements TolerantStrategy {
    @Override
    public <T> T doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务出错，触发快速失败", e);
    }
}

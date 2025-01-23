package org.howard.hrpc.fault.tolerant;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 容错策略-失败安全
 *
 * @Author <a href="https://github.com/howard-lew">HowardLiu</a>
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {
    @Override
    public <T> T doTolerant(Map<String, Object> context, Exception e) {
        log.info("服务出错，触发失败安全策略", e);

        // 返回 null 作为默认值，除非有更具体的类型可以返回
        @SuppressWarnings("unchecked")
        T defaultValue = (T) ((Class<T>) Object.class).cast(null);
        return defaultValue;
    }
}

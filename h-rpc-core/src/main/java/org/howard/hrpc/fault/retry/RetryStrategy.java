package org.howard.hrpc.fault.retry;

import java.util.concurrent.Callable;

/**
 * 重试策略
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
public interface RetryStrategy {
    /**
     * 重试
     *
     * @param callable
     * @return
     * @throws Exception
     */
    <T> T doRetry(Callable<T> callable) throws Exception;
}

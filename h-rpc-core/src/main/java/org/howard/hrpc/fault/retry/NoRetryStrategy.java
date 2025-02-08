package org.howard.hrpc.fault.retry;

import java.util.concurrent.Callable;

/**
 * 不重试
 *
 * @Author <a href="https://github.com/howard12358">HowardLiu</a>
 */
public class NoRetryStrategy implements RetryStrategy {

    @Override
    public <T> T doRetry(Callable<T> callable) throws Exception {
        return callable.call();
    }
}

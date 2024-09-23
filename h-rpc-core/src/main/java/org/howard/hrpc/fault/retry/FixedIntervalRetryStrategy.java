package org.howard.hrpc.fault.retry;

import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间间隔重试策略
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {

    @Override
    public <T> T doRetry(Callable<T> callable) throws Exception {
        Retryer<T> retryer = RetryerBuilder.<T>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数 {}", attempt.getAttemptNumber());
                    }
                })
                .build();
        return retryer.call(callable);
    }
}

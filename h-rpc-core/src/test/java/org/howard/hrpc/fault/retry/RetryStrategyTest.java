package org.howard.hrpc.fault.retry;

import org.howard.hrpc.fault.retry.NoRetryStrategy;
import org.howard.hrpc.fault.retry.RetryStrategy;
import org.howard.hrpc.model.RpcResponse;
import org.junit.Test;

/**
 * 重试策略测试
 */
public class RetryStrategyTest {

    RetryStrategy retryStrategy = new NoRetryStrategy();

    @Test
    public void doRetry() {
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("重试多次失败");
            e.printStackTrace();
        }
    }
}

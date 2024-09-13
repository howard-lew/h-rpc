package org.howard.example.consumer;

import org.howard.hrpc.config.RpcConfig;
import org.howard.hrpc.utils.ConfigUtils;

/**
 * 服务消费者示例
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
public class ConsumerExample {
    public static void main(String[] args) {
        RpcConfig hrpc = ConfigUtils.loadConfig(RpcConfig.class, "hrpc");
        System.out.println(hrpc);
    }
}

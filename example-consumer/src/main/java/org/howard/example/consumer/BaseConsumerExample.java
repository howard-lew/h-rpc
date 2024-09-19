package org.howard.example.consumer;

import org.howard.hrpc.config.RpcConfig;
import org.howard.hrpc.utils.ConfigUtils;

/**
 * @Author HowardLiu
 */
public class BaseConsumerExample {
    public static void main(String[] args) {
        RpcConfig hrpc = ConfigUtils.loadConfig(RpcConfig.class, "hrpc");
        System.out.println(hrpc);
    }
}

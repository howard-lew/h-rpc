package org.howard.hrpc.config;

import lombok.Data;

/**
 * RPC 框架配置选项
 *
 * @Author HowardLiu
 */
@Data
public class RpcConfig {
    /**
     * 服务名称
     */
    private String name = "h-rpc";
    /**
     * 服务版本
     */
    private String version = "1.0.0";
    /**
     * 服务主机
     */
    private String serverHost = "localhost";
    /**
     * 服务端口
     */
    private int port = 8080;
    /**
     * 是否开启 mock
     */
    private boolean mock = false;
}

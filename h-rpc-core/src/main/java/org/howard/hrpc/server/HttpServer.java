package org.howard.hrpc.server;

/**
 * HTTP 服务器接口
 *
 * @Author HowardLiu
 * 
 */
public interface HttpServer {
    /**
     * 启动服务
     *
     * @param port 端口
     */
    void doStart(int port);
}

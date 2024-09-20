package org.howard.hrpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static org.howard.hrpc.constant.RpcConstant.DEFAULT_SERVICE_VERSION;

/**
 * RPC 请求
 *
 * @Author HowardLiu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest implements Serializable {
    /**
     * 服务名
     */
    private String serviceName;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数
     */
    private Object[] args;
    /**
     * 参数类型
     */
    private Class<?>[] paramTypes;
    /**
     * 服务版本
     */
    private String serviceVersion = DEFAULT_SERVICE_VERSION;
}

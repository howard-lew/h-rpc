package org.howard.hrpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RPC 请求
 *
 * @Author HowardLiu
 * 
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
}

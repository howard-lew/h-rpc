package org.howard.hrpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.howard.hrpc.RpcApplication;
import org.howard.hrpc.model.RpcRequest;
import org.howard.hrpc.model.RpcResponse;
import org.howard.hrpc.serializer.Serializer;
import org.howard.hrpc.serializer.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 调用服务代理类
 *
 * @Author HowardLiu
 */

public class ServiceProxy implements InvocationHandler {
    private final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .args(args)
                .paramTypes(method.getParameterTypes())
                .build();

        try {
            byte[] serialized = serializer.serialize(rpcRequest);
            // todo 引入注册中心和服务发现机制
            try (HttpResponse response = HttpRequest.post("http://localhost:8080")
                    .body(serialized)
                    .execute()) {
                byte[] bytes = response.bodyBytes();

                RpcResponse rpcResponse = serializer.deserialize(bytes, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

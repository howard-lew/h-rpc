package org.howard.hrpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.howard.hrpc.model.RpcReq;
import org.howard.hrpc.model.RpcRes;
import org.howard.hrpc.serializer.JdkSerializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 调用服务代理类
 *
 * @Author HowardLiu
 * @Date 2024/9/13
 */

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        JdkSerializer serializer = new JdkSerializer();
        RpcReq rpcReq = RpcReq.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .args(args)
                .paramTypes(method.getParameterTypes())
                .build();

        try {
            byte[] serialized = serializer.serialize(rpcReq);
            // todo 引入注册中心和服务发现机制
            try (HttpResponse response = HttpRequest.post("http://localhost:8080")
                    .body(serialized)
                    .execute()) {
                byte[] bytes = response.bodyBytes();

                RpcRes rpcRes = serializer.deserialize(bytes, RpcRes.class);
                return rpcRes.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

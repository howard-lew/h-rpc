package org.howard.hrpc.server;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.howard.hrpc.model.RpcReq;
import org.howard.hrpc.model.RpcRes;
import org.howard.hrpc.registry.LocalRegistry;
import org.howard.hrpc.serializer.JdkSerializer;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * HTTP 请求处理
 *
 * @Author HowardLiu
 *
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        JdkSerializer serializer = new JdkSerializer();

        System.out.println("Received request: " + request.method() + " " + request.uri());

        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcReq rpcReq = null;
            try {
                rpcReq = serializer.deserialize(bytes, RpcReq.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            RpcRes rpcRes = new RpcRes();
            if (rpcReq == null) {
                rpcRes.setMessage("RPC Request is null");
                doResponse(request, serializer, rpcRes);
                return;
            }

            try {
                // 利用反射调用服务
                Class<?> serviceImplClass = LocalRegistry.get(rpcReq.getServiceName());
                Method method = serviceImplClass.getMethod(rpcReq.getMethodName(), rpcReq.getParamTypes());
                Object result = method.invoke(serviceImplClass.newInstance(), rpcReq.getArgs());

                rpcRes.setData(result);
                rpcRes.setDataType(method.getReturnType());
                rpcRes.setMessage("success");
            } catch (Exception e) {
                e.printStackTrace();
                rpcRes.setMessage(e.getMessage());
                rpcRes.setException(e);
            }

            doResponse(request, serializer, rpcRes);
        });
    }

    /**
     * 返回结果
     *
     * @param request
     * @param serializer
     * @param rpcRes
     */
    private void doResponse(HttpServerRequest request, JdkSerializer serializer, RpcRes rpcRes) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try {
            byte[] serialized = serializer.serialize(rpcRes);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}

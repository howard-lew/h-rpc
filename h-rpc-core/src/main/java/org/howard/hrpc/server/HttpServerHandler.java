package org.howard.hrpc.server;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.howard.hrpc.RpcApplication;
import org.howard.hrpc.model.RpcRequest;
import org.howard.hrpc.model.RpcResponse;
import org.howard.hrpc.registry.LocalRegistry;
import org.howard.hrpc.serializer.Serializer;
import org.howard.hrpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * HTTP 请求处理
 *
 * @Author HowardLiu
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    private final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

    @Override
    public void handle(HttpServerRequest request) {
        System.out.println("Received request: " + request.method() + " " + request.uri());

        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            RpcResponse rpcResponse = new RpcResponse();
            if (rpcRequest == null) {
                rpcResponse.setMessage("RPC Request is null");
                doResponse(request, serializer, rpcResponse);
                return;
            }

            try {
                // 利用反射调用服务
                Class<?> serviceImplClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = serviceImplClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
                Object result = method.invoke(serviceImplClass.newInstance(), rpcRequest.getArgs());

                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("success");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            doResponse(request, serializer, rpcResponse);
        });
    }

    /**
     * 返回结果
     *
     * @param request
     * @param serializer
     * @param rpcResponse
     */
    private void doResponse(HttpServerRequest request, Serializer serializer, RpcResponse rpcResponse) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try {
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}

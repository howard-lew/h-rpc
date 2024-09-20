package org.howard.hrpc.proxy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.howard.hrpc.RpcApplication;
import org.howard.hrpc.config.RegistryConfig;
import org.howard.hrpc.model.RpcRequest;
import org.howard.hrpc.model.RpcResponse;
import org.howard.hrpc.model.ServiceMetaInfo;
import org.howard.hrpc.registry.Registry;
import org.howard.hrpc.registry.RegistryFactory;
import org.howard.hrpc.serializer.Serializer;
import org.howard.hrpc.serializer.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import static org.howard.hrpc.constant.RpcConstant.DEFAULT_SERVICE_VERSION;

/**
 * 调用服务代理类
 *
 * @Author HowardLiu
 */

public class ServiceProxy implements InvocationHandler {
    private final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = method.getDeclaringClass().getName();

        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .args(args)
                .paramTypes(method.getParameterTypes())
                .build();

        try {
            byte[] serialized = serializer.serialize(rpcRequest);

            String serviceAddress = discoverAddress(serviceName);

            try (HttpResponse response = HttpRequest.post(serviceAddress)
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

    /**
     * 从注册中心获取服务地址
     *
     * @param serviceName
     * @return
     */
    private String discoverAddress(String serviceName) {
        // 引入注册中心和服务发现机制
        RegistryConfig registryConfig = RpcApplication.getRpcConfig().getRegistryConfig();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo() {{
            setServiceName(serviceName);
            setServiceVersion(DEFAULT_SERVICE_VERSION);
        }};
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollectionUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }
        // todo 负载均衡
        return serviceMetaInfoList.get(0).getServiceAddress();
    }
}

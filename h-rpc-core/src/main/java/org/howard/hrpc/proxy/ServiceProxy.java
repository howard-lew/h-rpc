package org.howard.hrpc.proxy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.howard.hrpc.RpcApplication;
import org.howard.hrpc.config.RegistryConfig;
import org.howard.hrpc.config.RpcConfig;
import org.howard.hrpc.fault.retry.RetryStrategy;
import org.howard.hrpc.fault.retry.RetryStrategyFactory;
import org.howard.hrpc.loadbalancer.LoadBalancer;
import org.howard.hrpc.loadbalancer.LoadBalancerFactory;
import org.howard.hrpc.model.RpcRequest;
import org.howard.hrpc.model.RpcResponse;
import org.howard.hrpc.model.ServiceMetaInfo;
import org.howard.hrpc.registry.Registry;
import org.howard.hrpc.registry.RegistryFactory;
import org.howard.hrpc.serializer.Serializer;
import org.howard.hrpc.serializer.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

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
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            byte[] serialized = serializer.serialize(rpcRequest);

            // 服务发现
            List<ServiceMetaInfo> serviceMetaInfoList = discoverServices(rpcConfig, serviceName);

            // 负载均衡
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancing(rpcConfig, rpcRequest, serviceMetaInfoList);

            HttpRequest httpRequest = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                    .body(serialized);
            // 使用重试机制
            RetryStrategy retry = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            try (HttpResponse response = retry.doRetry(new Callable<HttpResponse>() {
                @Override
                public HttpResponse call() {
                    return httpRequest.execute();
                }
            })) {
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
     * 服务负载均衡
     *
     * @param rpcConfig
     * @param rpcRequest
     * @param serviceMetaInfoList
     * @return
     */
    private ServiceMetaInfo loadBalancing(RpcConfig rpcConfig, RpcRequest rpcRequest, List<ServiceMetaInfo> serviceMetaInfoList) {
        LoadBalancer loadBalancer = LoadBalancerFactory.getLoadBalancer(rpcConfig.getLoadBalancer());
        HashMap<String, Object> requestParams = new HashMap<String, Object>() {{
            put("methodName", rpcRequest.getMethodName());
        }};
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
        return selectedServiceMetaInfo;
    }

    /**
     * 从注册中心获取服务地址
     *
     * @param rpcConfig
     * @param serviceName
     * @return
     */
    private List<ServiceMetaInfo> discoverServices(RpcConfig rpcConfig, String serviceName) {
        // 引入注册中心和服务发现机制
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo() {{
            setServiceName(serviceName);
            setServiceVersion(DEFAULT_SERVICE_VERSION);
        }};
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollectionUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }
        return serviceMetaInfoList;
    }
}

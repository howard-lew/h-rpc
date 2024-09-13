package org.howard.hrpc.proxy;

import java.lang.reflect.Proxy;

/**
 * @Author HowardLiu
 *
 */
public class ServiceProxyFactory {
    public static <T> T getProxy(Class<T> serviceInterface) {
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface}, new ServiceProxy());
    }
}

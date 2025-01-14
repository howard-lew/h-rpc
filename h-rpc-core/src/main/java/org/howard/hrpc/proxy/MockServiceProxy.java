package org.howard.hrpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock 服务代理
 *
 * @Author <a href="https://github.com/yige-howard">HowardLiu</a>
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 根据方法的返回值类型，生成特定的黑默以值对象
        Class<?> returnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultObj(returnType);
    }

    /**
     * 获取默认值对象
     *
     * @param type
     * @return
     */
    private Object getDefaultObj(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == int.class) {
                return 0;
            } else if (type == short.class) {
                return (short) 0;
            } else if (type == long.class) {
                return 0L;
            } else if (type == float.class) {
                return 0.0F;
            } else if (type == double.class) {
                return 0.0D;
            } else if (type == char.class) {
                return '\u0000';
            } else if (type == boolean.class) {
                return false;
            } else if (type == byte.class) {
                return (byte) 0;
            }
        }
        return null;
    }
}

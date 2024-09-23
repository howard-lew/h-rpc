package org.howard.hrpc.spring.boot.starter.annotation;

import org.howard.hrpc.spring.boot.starter.bootstrap.RpcConsumerBootstrap;
import org.howard.hrpc.spring.boot.starter.bootstrap.RpcInitBootstrap;
import org.howard.hrpc.spring.boot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用 Rpc 注解
 *
 * @Author <a href="https://github.com/weedsx">HowardLiu</a>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {
    /**
     * 需要启动 server
     *
     * @return
     */
    boolean needServer() default true;
}

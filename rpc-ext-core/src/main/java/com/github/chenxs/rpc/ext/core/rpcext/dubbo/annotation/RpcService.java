package com.github.chenxs.rpc.ext.core.rpcext.dubbo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 〈一句话功能简述〉<br>
 * Description: rpc服务描述信息
 *
 * @author hillchen
 * @create 2019/8/26 18:35
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService{
    /**
     * 服务版本
     * @return
     */
    String version() default "";

    /**
     * 服务自定义路经
     * @return
     */
    String path() default "";

    /**
     * 延迟注册时间
     * @return
     */
    int delay() default 0;

    /**
     * 超时时间
     * @return
     */
    int timeout() default 0;

    /**
     * 重试次数
     * @return
     */
    int retries() default 0;

    /**
     * 服务端服务的最大连接数
     * @return
     */
    int connections() default 0;

    /**
     * 负载均衡策略
     * @return
     */
    String loadbalance() default "";

    /**
     * 异步执行
     * @return
     */
    boolean async() default false;

    /**
     * 是否注册到注册中心
     * @return
     */
    boolean register() default true;

    /**
     * 访问日志
     * @return
     */
    String accesslog() default "";

    /**
     * 集群容错
     * @return
     */
    String cluster() default "";

    /**
     * 协议
     * @return
     */
    String[] protocol() default {};

    /**
     * 客户端使用时检测
     * @return
     */
    boolean check() default false;

    /**
     * 生成动态代理方式
     * @return
     */
    String proxy() default "";

    /**
     * 每服务消费者每服务每方法最大并发调用数
     * @return
     */
    int actives() default 0;
}

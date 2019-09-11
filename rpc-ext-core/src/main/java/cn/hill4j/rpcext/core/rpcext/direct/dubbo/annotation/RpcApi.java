package cn.hill4j.rpcext.core.rpcext.direct.dubbo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 2019/8/26 18:35 <br>
 * Description: rpc服务描述信息
 *
 * @author hillchen
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcApi {
    /**
     * 服务版本
     * @return 版本号
     */
    String version() default "";

    /**
     * 服务自定义路经
     * @return 服务自定义路经
     */
    String path() default "";

    /**
     * 延迟注册时间
     * @return 延迟注册时间
     */
    int delay() default 0;

    /**
     * 超时时间
     * @return 超时时间
     */
    int timeout() default 0;

    /**
     * 超时时间
     * @return 超时时间
     */
    int retries() default 0;

    /**
     * 服务端服务的最大连接数
     * @return 服务端服务的最大连接数
     */
    int connections() default 0;

    /**
     * 负载均衡策略
     * @return 负载均衡策略
     */
    String loadbalance() default "";

    /**
     * 异步执行
     * @return 异步执行
     */
    boolean async() default false;

    /**
     * 是否注册到注册中心
     * @return 是否注册到注册中心
     */
    boolean register() default true;

    /**
     * 访问日志
     * @return 访问日志
     */
    String accesslog() default "";

    /**
     * 集群容错
     * @return 集群容错
     */
    String cluster() default "";

    /**
     * 协议
     * @return 协议
     */
    String[] protocol() default {};

    /**
     * 客户端使用时检测
     * @return 客户端使用时检测
     */
    boolean check() default false;

    /**
     * 生成动态代理方式
     * @return 生成动态代理方式
     */
    String proxy() default "";

    /**
     * 每服务消费者每服务每方法最大并发调用数
     * @return 每服务消费者每服务每方法最大并发调用数
     */
    int actives() default 0;
}

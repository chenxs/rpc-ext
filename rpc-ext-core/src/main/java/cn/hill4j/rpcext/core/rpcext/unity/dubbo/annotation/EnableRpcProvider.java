package cn.hill4j.rpcext.core.rpcext.unity.dubbo.annotation;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import cn.hill4j.rpcext.core.rpcext.unity.dubbo.RpcProviderRegistrar;
/**
 * 2019/9/14 23:25<br>
 * Description: rpc服务启动标注注解
 *
 * @author hillchen
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@EnableDubboConfig
@Import(RpcProviderRegistrar.class)
public @interface EnableRpcProvider {
    /**
     *
     * @return 需要将本应用注册为api服务提供者的api应用名列表,如果返回为空则所有被
     * RpcApi注解标注的接口实现类都会暴露注册中心
     */
    String[] value() default {};

    /**
     *
     * @return 扫描指定包下所有被RpcApi标注的接口暴露为rpc服务
     */
    String[] basePackages() default{};

    /**
     *
     * @return 不需要暴露为rpc服务的应用名集合
     */
    String[] excludedAppNames() default {};

    /**
     *
     * @return 不需要暴露为rpc服务的包集合
     */
    String[] excludedPackages() default {};
}

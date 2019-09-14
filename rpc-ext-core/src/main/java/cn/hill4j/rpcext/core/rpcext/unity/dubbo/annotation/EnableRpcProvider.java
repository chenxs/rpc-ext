package cn.hill4j.rpcext.core.rpcext.unity.dubbo.annotation;

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
@Import(RpcProviderRegistrar.class)
public @interface EnableRpcProvider {
    /**
     *
     * @return 需要将本应用注册为api服务提供者的api应用名列表,如果返回为空则所有被
     * RpcApi注解标注的接口实现类都会暴露注册中心
     */
    String[] value() default {};
}

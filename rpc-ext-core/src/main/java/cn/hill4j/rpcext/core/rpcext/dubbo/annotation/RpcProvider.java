package cn.hill4j.rpcext.core.rpcext.dubbo.annotation;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 2019/9/11 15:42<br>
 * Description: 服务提供者注释
 *
 * @author hillchen
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcProvider {
    /**
     * @return 需要暴露为服务提供者的应用名列表
     */
    @AliasFor(annotation = RpcProvider.class, attribute = "value")
    String[] exportAppNames() default  {};

    /**
     * @return 需要暴露为服务提供者的应用名列表
     */
    @AliasFor(annotation = RpcProvider.class, attribute = "exportAppNames")
    String[] value() default  {};
}

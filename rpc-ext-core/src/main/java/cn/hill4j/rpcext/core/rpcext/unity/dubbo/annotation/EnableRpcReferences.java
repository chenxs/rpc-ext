package cn.hill4j.rpcext.core.rpcext.unity.dubbo.annotation;

import cn.hill4j.rpcext.core.rpcext.unity.dubbo.RpcReferenceRegistrar;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 2019/9/17 22:34<br>
 * Description: rpc 引用bean配置信息扫描标注
 *
 * @author hillchen
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@EnableDubboConfig
@Import(RpcReferenceRegistrar.class)
public @interface EnableRpcReferences {
    /**
     *
     * @return 需要注册为rpc客户端的应用
     */
    String[] referenceAppNames() default {};

    /**
     *
     * @return 开发组织项目的根包列表
     */
    String[] basePackages() default {};
}

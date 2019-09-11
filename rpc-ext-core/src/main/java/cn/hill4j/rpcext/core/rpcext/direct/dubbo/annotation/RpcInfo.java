package cn.hill4j.rpcext.core.rpcext.direct.dubbo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 〈2019/8/26 18:35 <br>
 * Description: rpc服务描述信息
 *
 * @author hillchen
 */
@Target({ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcInfo {
    /**
     * 应用名
     * @return 应用名
     */
    String appName ();
}

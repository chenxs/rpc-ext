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
@Target({ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcInfo {
    /**
     * 应用名
     * @return
     */
    String appName ();
}

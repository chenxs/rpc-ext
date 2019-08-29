package com.hill.rpc.ext.core.utils;

import com.hill.rpc.ext.core.rpcext.dubbo.exception.ResetRpcDefinitionErrorException;
import com.hill.rpc.ext.core.utils.exception.ReflectException;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * 〈一句话功能简述〉<br>
 * Description: ReflectUtils
 *
 * @author hillchen
 * @create 2019/8/30 01:03
 */
public class ReflectUtils {
    private ReflectUtils(){}


    public static Object getBeanFieldVal(Object bean, String fieldName){
        try {
            Field field = ReflectionUtils.findField(bean.getClass(),fieldName);
            field.setAccessible(true);
            return field.get(bean);
        } catch (IllegalAccessException e) {
            throw new ReflectException("get cacheInjectionMetadata error.");
        }
    }
}
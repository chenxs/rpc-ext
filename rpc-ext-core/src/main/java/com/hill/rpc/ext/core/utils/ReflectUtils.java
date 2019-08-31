package com.hill.rpc.ext.core.utils;

import com.hill.rpc.ext.core.rpcext.dubbo.exception.ResetRpcDefinitionErrorException;
import com.hill.rpc.ext.core.utils.exception.ReflectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 〈一句话功能简述〉<br>
 * Description: ReflectUtils
 *
 * @author hillchen
 * @create 2019/8/30 01:03
 */
public class ReflectUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(ReflectUtils.class);
    private ReflectUtils(){}


    public static Object getBeanFieldVal(Object bean, String fieldName){
        try {
            Field field = ReflectionUtils.findField(bean.getClass(),fieldName);
            field.setAccessible(true);
            return field.get(bean);
        } catch (IllegalAccessException e) {
            throw new ReflectException("get bean filed value error.");
        }
    }

    public static Object getBeanFieldValNoError(Object bean, String fieldName){
        try {
            Field field = ReflectionUtils.findField(bean.getClass(),fieldName);
            field.setAccessible(true);
            return field.get(bean);
        } catch (IllegalAccessException e) {
            LOGGER.info("get bean filed value error.");
            return null;
        }
    }

    public static Object invokeMethod(Object bean,String methodName,Class[] paramsTypes,Object... params) {
        if (bean != null){
            try {
                Method method = bean.getClass().getDeclaredMethod(methodName,paramsTypes);
                method.setAccessible(true);
                return method.invoke(bean,params);
            } catch (NoSuchMethodException|InvocationTargetException|IllegalAccessException e) {
                throw new ReflectException("invork method error.",e);
            }
        }
        return null;
    }
}
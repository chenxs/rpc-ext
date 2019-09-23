package cn.hill4j.rpcext.core.utils;

import cn.hill4j.rpcext.core.utils.exception.ReflectException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 2019/8/30 01:03<br>
 * Description: ReflectUtils
 *
 * @author hillchen
 */
public class ReflectUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(ReflectUtils.class);
    private ReflectUtils(){}

    /**
     * 通过反射获取指定bean上指定字段的值，即使是私有字段也是可以获取到的
     * 如果获取失败则抛出异常com.hill.rpc.ext.core.utils.exception.ReflectException
     * @param bean bean对象
     * @param fieldName 需要获取字段值的字段名
     * @return 字段值
     */
    public static Object getBeanFieldVal(Object bean, String fieldName){
        try {
            Field field = ReflectionUtils.findField(bean.getClass(),fieldName);
            field.setAccessible(true);
            return field.get(bean);
        } catch (IllegalAccessException e) {
            throw new ReflectException("get bean filed value error.");
        }
    }

    /**
     * 通过反射获取指定bean上指定字段的值，即使是私有字段也是可以获取到的
     * 如果获取失败则返回空
     * @param bean bean对象
     * @param fieldName 需要获取字段值的字段名
     * @return 返回字段值， 如果获取失败则返回null
     */
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

    /**
     * 通过反射获取指定bean上指定字段的值，即使是私有字段也是可以获取到的
     * 如果获取失败则抛出异常com.hill.rpc.ext.core.utils.exception.ReflectException
     * @param bean bean对象
     * @param fieldName 需要设值的字段名
     * @param value 需要设值的字段值
     *
     */
    public static void setBeanFieldVal(Object bean, String fieldName,Object value){
        try {
            Field field = ReflectionUtils.findField(bean.getClass(),fieldName);
            field.setAccessible(true);
            field.set(bean,value);
        } catch (IllegalAccessException e) {
            throw new ReflectException("get bean filed value error.");
        }
    }

    /**
     * 通过反射执行指定类的指定方法，即使是使用方法也是可以执行
     * 如果bean 为空则返回null,
     * 如果需要执行的方法不能找到则抛出异常com.hill.rpc.ext.core.utils.exception.ReflectException
     * @param bean bean对象
     * @param methodName 方法名
     * @param paramsTypes 方法入参类型数组
     * @param params 方法入参数组
     * @return 执行方法的返回结果
     */
    public static Object invokeMethod(Object bean,String methodName,Class[] paramsTypes,Object... params) {
        if (bean != null){
            Method method ;
            if (paramsTypes == null || paramsTypes.length == 0){
                method = ReflectionUtils.findMethod(bean.getClass(),methodName) ;
            }else {
                method = ReflectionUtils.findMethod(bean.getClass(),methodName,paramsTypes);
            }
            if (method == null){
                throw new ReflectException(String.format("method[%s.%s] no found,paramsTypes:(%s)",bean.getClass().getName(),methodName, JSONObject.toJSONString(paramsTypes)));
            }
            method.setAccessible(true);
            try {
                return method.invoke(bean,params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ReflectException("invokeMethod error",e);
            }
        }
        return null;
    }

    /**
     * 递归获取类实现的所有接口,包括父类实现的接口
     * @param clazz 需要检查的类
     * @return 类实现的所有接口
     */
    public static Set<Class> getAllInterfaces(Class clazz){
        Set<Class> classSet = new HashSet<>();
        Class[] interfaces = clazz.getInterfaces();
        if (interfaces != null){
            classSet.addAll(Arrays.asList(interfaces));
        }
        Class supClazz = clazz.getSuperclass();
        if (supClazz  != null && !Objects.equals(supClazz,Object.class) ){
            classSet.addAll(getAllInterfaces(supClazz));
            LOGGER.info("supClazz:" + supClazz.getName());
        }
        return classSet;
    }
}

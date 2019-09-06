package cn.hill4j.rpcext.core.utils;

import cn.hill4j.rpcext.core.utils.exception.ReflectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

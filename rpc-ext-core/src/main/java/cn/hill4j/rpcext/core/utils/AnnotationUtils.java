package cn.hill4j.rpcext.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 *  2019/8/25 11:03 <br>
 * Description: 注解工具类
 *
 * @author hillchen
 */
public class AnnotationUtils {
    private final static String memberValuesField = "memberValues";

    private AnnotationUtils(){}

    /**
     * 递归获取包或者父包上的注解，直到第一个注解，如果没有找到注解则返回空
     * @param pkg 子包对象
     * @param anno 查找的注解类型
     * @param <T>  查找的注解类型
     * @return 返回包或者父包上的注解，直到第一个注解，如果没有找到注解则返null
     */
    public static <T extends Annotation> T recursionGet(Package pkg,Class<T> anno){
        if (pkg.isAnnotationPresent(anno)){
            return pkg.getAnnotation(anno);
        }else {
            Package parentPck = PackageUtils.getParent(pkg);
            if (parentPck != null){
                return recursionGet(parentPck, anno);
            }
        }
        return null;
    }

    /**
     * 递归获取类、包或者父包上的注解，直到第一个注解，如果没有找到注解则返回空
     * @param clazz 类类型
     * @param anno 查找的注解类型
     * @param <T> 查找的注解类型
     * @return 返回类、包或者父包上的注解，直到第一个注解，如果没有找到注解则返回空
     */
    public static <T extends Annotation> T recursionGet(Class clazz,Class<T> anno){
        if (clazz.isAnnotationPresent(anno)){
            return (T) clazz.getAnnotation(anno);
        }else {
            return recursionGet(clazz.getPackage(), anno);
        }
    }

    /**
     * 递归获取类、包或者父包上的注解，直到第一个注解，如果没有找到注解则返回空
     * @param className 类全路径名
     * @param anno 查找的注解类型
     * @param <T> 查找的注解类型
     * @return 返回类、包或者父包上的注解，直到第一个注解，如果没有找到注解则返回空
     * @throws ClassNotFoundException 如果没有找到类名对应的类，抛ClassNotFoundException异常
     */
    public static <T extends Annotation> T recursionGet(String className,Class<T> anno) throws ClassNotFoundException {
        Class clazz = Class.forName(className);
        return recursionGet(clazz, anno);
    }

    /**
     * 递归获取方法、类、包或者父包上的注解，直到第一个注解，如果没有找到注解则返回空
     * @param method 方法名
     * @param anno 查找的注解类型
     * @param <T> 查找的注解类型
     * @return 返回方法、类、包或者父包上的注解，直到第一个注解，如果没有找到注解则返回空
     */
    public static <T extends Annotation> T recursionGet(Method method, Class<T> anno){
        if (method.isAnnotationPresent(anno)){
            return method.getAnnotation(anno);
        }else {
            return recursionGet(method.getDeclaringClass(), anno);
        }
    }

    /**
     * 动态设置注解上的值
     * @param annotationObj 注解对象
     * @param fieldName 需要设置的注解字段名
     * @param FieldVal 需要设置的注解值
     */
    public static void setAnnotationFieldVal(Object annotationObj,String fieldName , Object FieldVal){
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotationObj);
        Map<String, Object> memberValues = (Map<String, Object>) ReflectUtils.getBeanFieldVal(invocationHandler,memberValuesField);
        memberValues.put(fieldName,FieldVal);
    }
}

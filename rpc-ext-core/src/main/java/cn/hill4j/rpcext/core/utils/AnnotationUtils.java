package cn.hill4j.rpcext.core.utils;

import cn.hill4j.rpcext.core.utils.exception.ReflectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationType;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

/**
 *  2019/8/25 11:03 <br>
 * Description: 注解工具类
 *
 * @author hillchen
 */
public class AnnotationUtils {
    private  static Logger logger = LoggerFactory.getLogger(AnnotationUtils.class);
    private final static String memberValuesField = "memberValues";
    private static Class ANNOTATION_INVOCATION_HANDLER_CLAZZ = getAnnotationInvocationHandlerClazz();
    private static Field ANNOTATION_MEMBER_VALUES_FIELD = getAnnotationMemberValuesGetField();

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
     * @param <T> 需要设值的注解类型
     */
    public static <T extends Annotation> void setAnnotationFieldVal(T annotationObj,String fieldName , Object FieldVal){
        try {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotationObj);
            Map<String, Object> memberValues = (Map<String, Object>) ANNOTATION_MEMBER_VALUES_FIELD.get(invocationHandler);
            memberValues.put(fieldName,FieldVal);
        }catch (Exception e){
            logger.error("setAnnotationFieldVal  erro",e);
            throw new ReflectException(e);
        }
    }

    /**
     * 创建指定注解对象
     * @param annotationClass 注解对象类型
     * @param annotationParams 注解对象属性组
     * @param <T> 注解对象类型
     * @return 创建的注解对象
     */
    public static <T extends Annotation> T createAnnotationObj (Class<T> annotationClass , final Map<String,Object> annotationParams)  {
        AnnotationType annotationType = AnnotationType.getInstance(annotationClass);
        Map<String, Object>  memberDefaults = annotationType.memberDefaults();
        Map<String, Class<?>>  memberTypes = annotationType.memberTypes();
        final Map<String, Object>  memberVals = new HashMap<>(memberDefaults.size());
        if (annotationParams != null){
            memberDefaults.forEach((key,val) -> {
                Object paramVal = annotationParams.get(key);
                Class memberType = memberTypes.get(key);
                if (paramVal != null && paramVal.getClass().isAssignableFrom(memberType)){
                    memberVals.put(key,paramVal);
                }else {
                    memberVals.put(key,val);
                }
            });
        }
        return (T)AnnotationParser.annotationForMap(annotationClass,memberVals);
    }

    /**
     * 将原注解对象装欢成目标注解对象
     * @param sourceAnnotation 原注解对象
     * @param target 目标注解对象类型
     * @param <S> 原注解对象类型
     * @param <T> 目标注解对象类型
     * @return 目标注解对象
     */
    public static <S extends Annotation, T extends Annotation> T transformToOther (S sourceAnnotation,Class<T> target) {
        try {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(sourceAnnotation);
            Map<String, Object> memberValues = (Map<String, Object>)ANNOTATION_MEMBER_VALUES_FIELD.get(invocationHandler);
            return createAnnotationObj(target,memberValues);
        }catch (Exception e){
            logger.error("transformToOther error",e);
            throw new ReflectException(e);
        }

    }

    private static Class getAnnotationInvocationHandlerClazz (){
        try {
            return   Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        }catch (Exception e){
            logger.error("getAnnotationInvocationHandlerClazz  erro",e);
            throw new ReflectException(e);
        }
    }

    private static Field getAnnotationMemberValuesGetField(){
        Field field = ReflectionUtils.findField(ANNOTATION_INVOCATION_HANDLER_CLAZZ,memberValuesField);
        field.setAccessible(true);
        return field;
    }
    private static Constructor getAnnotationInvocationHandlerConstructor(){
        try {
            Constructor ctor  = ANNOTATION_INVOCATION_HANDLER_CLAZZ.getDeclaredConstructor(Class.class, Map.class);
            ctor.setAccessible(true);
            return ctor;
        } catch (NoSuchMethodException e) {
            logger.error("getAnnotationInvocationHandlerConstructor  erro",e);
            throw new ReflectException(e);
        }
    }
}

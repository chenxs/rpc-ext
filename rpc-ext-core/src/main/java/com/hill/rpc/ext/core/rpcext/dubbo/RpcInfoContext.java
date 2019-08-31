package com.hill.rpc.ext.core.rpcext.dubbo;

import com.hill.rpc.ext.core.rpcext.dubbo.annotation.RpcInfo;
import com.hill.rpc.ext.core.utils.AnnotationUtils;
import com.hill.rpc.ext.core.utils.ReflectUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.core.env.StandardEnvironment;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈一句话功能简述〉<br>
 * Description: RpcInfoContext
 *
 * @author hillchen
 * @create 2019/8/30 01:24
 */
public class RpcInfoContext {
    private RpcInfoContext(){}

    public static final String appResetPrefix = "rpc.reset";
    public static final String urlPropertyName = "url";
    public static final String timeoutPropertyName = "timeout";
    public static final String interfacePropertyName = "interface";
    public static final String referenceField = "reference";
    public static final String popFieldName = "field";

    public static ConcurrentHashMap<String, RpcInfo> rpcInfoMap = new ConcurrentHashMap<>();
    public static RpcInfo getAppRpcInfo(String interfaceName) {
        if (rpcInfoMap.containsKey(interfaceName)){
            return rpcInfoMap.get(interfaceName);
        }else {
            RpcInfo rpcInfo = null;
            try {
                rpcInfo = AnnotationUtils.recursionGet(interfaceName, RpcInfo.class);
                if (rpcInfo != null){
                    rpcInfoMap.putIfAbsent(interfaceName,rpcInfo);
                }
            } catch (ClassNotFoundException e) {
                // 忽略
            }
            return rpcInfo;
        }
    }

    public static RpcInfo getAppRpcInfo(Class interfaceClazz) {
        String interfaceName = interfaceClazz.getName();
        if (rpcInfoMap.containsKey(interfaceName)){
            return rpcInfoMap.get(interfaceName);
        }else {
            RpcInfo rpcInfo = AnnotationUtils.recursionGet(interfaceClazz, RpcInfo.class);
            if (rpcInfo != null){
                rpcInfoMap.putIfAbsent(interfaceName,rpcInfo);
            }
            return rpcInfo;
        }
    }

    public static String getRpcResetKey (String appName,String parameName){
        return new StringBuilder(appResetPrefix).append(".").append(appName).append(".").append(parameName).toString();
    }

    public static boolean needResetToDirect(Class referenClazz, StandardEnvironment env ){
        RpcInfo rpcInfo = getAppRpcInfo(referenClazz);
        return needResetToDirect(rpcInfo,env);
    }

    public static boolean needResetToDirect(RpcInfo rpcInfo,StandardEnvironment env ){
        if (rpcInfo != null ){
            String appName = rpcInfo.appName();
            String rpcUrlResetKey = RpcInfoContext.getRpcResetKey(appName,urlPropertyName);
            return env.containsProperty(rpcUrlResetKey);
        }
        return false;
    }
    public static void resetToDirect(StandardEnvironment env, MutablePropertyValues mutablePropertyValues ){
        String referenClazzName = mutablePropertyValues.getPropertyValue(interfacePropertyName).getValue().toString();

        RpcInfo rpcInfo = getAppRpcInfo(referenClazzName);
        if (rpcInfo != null && needResetToDirect(rpcInfo,env)){
            String rpcUrlResetKey = RpcInfoContext.getRpcResetKey(rpcInfo.appName(),urlPropertyName);
            String url = env.getProperty(rpcUrlResetKey);
            mutablePropertyValues.removePropertyValue(RpcInfoContext.urlPropertyName);
            mutablePropertyValues.add(RpcInfoContext.urlPropertyName,url);

            // 当存在点对点直连时，所在点对点服务的超时时间为5分钟
            mutablePropertyValues.removePropertyValue(timeoutPropertyName);
            mutablePropertyValues.add(timeoutPropertyName,1000*60*5);
        }
    }

    public static void resetToDirect(StandardEnvironment env, InjectionMetadata.InjectedElement element){
        RpcInfo rpcInfo = null;

        Object reference = ReflectUtils.getBeanFieldValNoError(element,referenceField);
        Field field = (Field)ReflectUtils.getBeanFieldValNoError(element,popFieldName);
        if (field != null && reference !=  null ){
            Class fieldClazz = field.getType();
            rpcInfo = RpcInfoContext.getAppRpcInfo(fieldClazz);
        }
        if (needResetToDirect(rpcInfo,env)){
            String appName = rpcInfo.appName();
            String rpcUrlResetKey = RpcInfoContext.getRpcResetKey(appName, urlPropertyName);
            if (env.containsProperty(rpcUrlResetKey)){
                String url = env.getProperty(rpcUrlResetKey);
                AnnotationUtils.setAnnotationFieldVal(reference,urlPropertyName,url);
                AnnotationUtils.setAnnotationFieldVal(reference,timeoutPropertyName,1000*60*5);
            }
        }
    }

    public static String getDirectUrl(String appName , StandardEnvironment env){
        String rpcUrlResetKey = RpcInfoContext.getRpcResetKey(appName, urlPropertyName);
        return env.getProperty(rpcUrlResetKey);
    }
}
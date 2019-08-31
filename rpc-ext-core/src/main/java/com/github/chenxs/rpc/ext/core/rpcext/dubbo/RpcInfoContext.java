package com.github.chenxs.rpc.ext.core.rpcext.dubbo;

import com.github.chenxs.rpc.ext.core.rpcext.dubbo.annotation.RpcInfo;
import com.github.chenxs.rpc.ext.core.utils.AnnotationUtils;
import com.github.chenxs.rpc.ext.core.utils.ReflectUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 〈一句话功能简述〉<br>
 * Description: RpcInfoContext
 *
 * @author hillchen
 * @create 2019/8/30 01:24
 */
public class RpcInfoContext {
    private RpcInfoContext(){}

    private static final String appResetPrefix = "rpc.reset";
    private static final String urlPropertyName = "url";
    private static final String apiPackagePropertyName = "apiPackage";
    private static final String timeoutPropertyName = "timeout";
    private static final String interfacePropertyName = "interface";
    private static final String referenceField = "reference";
    private static final String popFieldName = "field";
    private static final String apiPackagePattern = "rpc\\.reset\\..+\\.apiPackage" ;
    private static boolean pkgAppNameMapInitFlag = false;

    private static final ConcurrentHashMap<String, RpcInfo> rpcInfoMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> pkgAppNameMap = new ConcurrentHashMap<>();

    /**
     * 获取接口上的RpcInfo注解
     * @param interfaceClazz
     * @return
     */
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

    /**
     * 获取rpcInfo的缓存key
     * @param appName
     * @param parameName
     * @return
     */
    private static String getRpcResetKey (String appName,String parameName){
        return new StringBuilder(appResetPrefix).append(".").append(appName).append(".").append(parameName).toString();
    }


    /**
     * 直连重置校验
     * @param appName
     * @param env
     * @return
     */
    public static boolean needResetToDirect(String appName,StandardEnvironment env ){
        if (StringUtils.hasText(appName)){
            String rpcUrlResetKey = RpcInfoContext.getRpcResetKey(appName,urlPropertyName);
            return env.containsProperty(rpcUrlResetKey);
        }
        return false;
    }

    /**
     * 直连重置校验
     * @param interfaceClazz
     * @param env
     * @return
     */
    public static boolean needResetToDirect(Class interfaceClazz,StandardEnvironment env ){
        String appName = getAppName(interfaceClazz,env);
        return needResetToDirect(appName,env);
    }

    /**
     * 重置点对点直连参数
     * @param env
     * @param mutablePropertyValues
     */
    public static void resetToDirect(StandardEnvironment env, MutablePropertyValues mutablePropertyValues ){
        String referenClazzName = mutablePropertyValues.getPropertyValue(interfacePropertyName).getValue().toString();
        String appName = getAppName(referenClazzName,env);
        if (needResetToDirect(appName,env)){
            String rpcUrlResetKey = RpcInfoContext.getRpcResetKey(appName,urlPropertyName);
            String url = env.getProperty(rpcUrlResetKey);
            mutablePropertyValues.removePropertyValue(RpcInfoContext.urlPropertyName);
            mutablePropertyValues.add(RpcInfoContext.urlPropertyName,url);

            // 当存在点对点直连时，所在点对点服务的超时时间为5分钟
            mutablePropertyValues.removePropertyValue(timeoutPropertyName);
            mutablePropertyValues.add(timeoutPropertyName,1000*60*5);
        }
    }

    /**
     * 重置点对点直连参数
     * @param env
     * @param element
     */
    public static void resetToDirect(StandardEnvironment env, InjectionMetadata.InjectedElement element){
        String appName = null;

        Object reference = ReflectUtils.getBeanFieldValNoError(element,referenceField);
        Field field = (Field)ReflectUtils.getBeanFieldValNoError(element,popFieldName);
        if (field != null && reference !=  null ){
            Class fieldClazz = field.getType();
            appName = getAppName(fieldClazz,env);
        }
        if (needResetToDirect(appName,env)){
            String rpcUrlResetKey = RpcInfoContext.getRpcResetKey(appName, urlPropertyName);
            if (env.containsProperty(rpcUrlResetKey)){
                String url = env.getProperty(rpcUrlResetKey);
                AnnotationUtils.setAnnotationFieldVal(reference,urlPropertyName,url);
                AnnotationUtils.setAnnotationFieldVal(reference,timeoutPropertyName,1000*60*5);
            }
        }
    }

    /**
     * 获取直连url
     * @param referenClazz
     * @param env
     * @return
     */
    public static String getDirectUrl(Class referenClazz , StandardEnvironment env){
        String appName = getAppName(referenClazz,env);
        String rpcUrlResetKey = RpcInfoContext.getRpcResetKey(appName, urlPropertyName);
        return env.getProperty(rpcUrlResetKey);
    }

    private synchronized static void initAppName(StandardEnvironment env){
        if (!pkgAppNameMapInitFlag){
            Map<String, Object> envMap = env.getSystemProperties() ;
            if (!CollectionUtils.isEmpty(envMap)){
                envMap.entrySet().forEach(entry -> {
                    String key = entry.getKey();

                    if (Pattern.matches(apiPackagePattern,key)){
                        String appName = key.replace(appResetPrefix+".","").replace("."+apiPackagePropertyName,"");
                        pkgAppNameMap.putIfAbsent(entry.getValue().toString(),appName);
                    }

                });
            }
            pkgAppNameMapInitFlag = true;
        }

    }

    private static String getAppName(Class referenClazz, StandardEnvironment env){
        initAppName(env);
        String pkgName = referenClazz.getPackage().getName();
        if (pkgAppNameMap.containsKey(pkgName)){
            return pkgAppNameMap.get(pkgName);
        }else {
            RpcInfo rpcInfo = getAppRpcInfo(referenClazz);
            if (rpcInfo != null){
                pkgAppNameMap.putIfAbsent(pkgName,rpcInfo.appName());
                return rpcInfo.appName();
            }
        }
        return null;
    }

    private static String getAppName(String referenClazz, StandardEnvironment env){
        try {
            Class clazz = Class.forName(referenClazz);
            return getAppName(clazz,env);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
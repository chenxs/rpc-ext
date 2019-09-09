package cn.hill4j.rpcext.core.rpcext.dubbo;

import cn.hill4j.rpcext.core.rpcext.dubbo.annotation.RpcInfo;
import cn.hill4j.rpcext.core.utils.AnnotationUtils;
import cn.hill4j.rpcext.core.utils.PackageUtils;
import cn.hill4j.rpcext.core.utils.ReflectUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 2019/8/30 01:24 <br>
 * Description: RpcInfoContext
 *
 * @author hillchen
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
    private static final String apiPackagePattern = "^rpc\\.reset\\..+\\.apiPackage$" ;
    private static final String packageUrlPattern = "^rpc\\.pkg\\.reset\\..+\\.url$" ;
    private static final String appUrlPattern = "^rpc\\.reset\\..+\\.url$" ;
    private static boolean pkgAppNameMapInitFlag = false;

    private static final ConcurrentHashMap<String, Optional<RpcInfo>> rpcInfoMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Optional<String>> pkgAppNameMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Optional<String>> appUrlMap = new ConcurrentHashMap<>();

    /**
     * 获取接口上的RpcInfo注解
     * @param interfaceClazz 接口类型
     * @return 该类/包或祖宗包上第一次出现RpcInfo的注解对象，否则返回null,同时将RpcInfo对象存到上下文缓存中
     */
    public static RpcInfo getAppRpcInfo(Class interfaceClazz) {
        String interfaceName = interfaceClazz.getName();
        if (rpcInfoMap.containsKey(interfaceName)){
            return rpcInfoMap.get(interfaceName).orElse(null);
        }else {
            RpcInfo rpcInfo = AnnotationUtils.recursionGet(interfaceClazz, RpcInfo.class);
            rpcInfoMap.putIfAbsent(interfaceName,Optional.ofNullable(rpcInfo));
            return rpcInfo;
        }
    }


    /**
     * 直连重置校验
     * @param appName 应用名
     * @param env spring容器上下文
     * @return 需要直连重置，则返回true需要直连重置，则返回false
     */
    public static boolean needResetToDirect(String appName,StandardEnvironment env ){
        initAppName(env);
        if (StringUtils.hasText(appName) && appUrlMap.containsKey(appName)){
            return appUrlMap.get(appName).isPresent();
        }
        return false;
    }

    /**
     * 直连重置校验
     * @param interfaceClazz 接口类型
     * @param env spring容器上下文
     * @return 需要直连重置，则返回true需要直连重置，则返回false
     */
    public static boolean needResetToDirect(Class interfaceClazz,StandardEnvironment env ){
        String appName = getAppName(interfaceClazz,env);
        return needResetToDirect(appName,env);
    }

    /**
     * 重置点对点直连参数
     * @param env spring容器上下文
     * @param mutablePropertyValues dubbo ReferenceBean中的属性配置集合
     *
     */
    public static void resetToDirect(StandardEnvironment env, MutablePropertyValues mutablePropertyValues ){
        String referenClazzName = mutablePropertyValues.getPropertyValue(interfacePropertyName).getValue().toString();
        String appName = getAppName(referenClazzName,env);
        if (needResetToDirect(appName,env)){
            String url = getDirectUrl(appName,env);
            if (StringUtils.hasText(url)){
                mutablePropertyValues.removePropertyValue(RpcInfoContext.urlPropertyName);
                mutablePropertyValues.add(RpcInfoContext.urlPropertyName,url);

                // 当存在点对点直连时，所在点对点服务的超时时间为5分钟
                mutablePropertyValues.removePropertyValue(timeoutPropertyName);
                mutablePropertyValues.add(timeoutPropertyName,1000*60*5);
            }
        }
    }

    /**
     * 重置点对点直连参数
     * @param env spring容器上下文
     * @param element spring bean中带有Reference注解的字段
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
            String url = getDirectUrl(appName,env);
            if (StringUtils.hasText(url)){
                AnnotationUtils.setAnnotationFieldVal(reference,urlPropertyName,url);
                AnnotationUtils.setAnnotationFieldVal(reference,timeoutPropertyName,1000*60*5);
            }
        }
    }

    /**
     * 获取直连url
     * @param referenClazz Reference接口类型
     * @param env spring容器上下文
     * @return 返回直连url
     */
    public static String getDirectUrl(Class referenClazz , StandardEnvironment env){
        initAppName(env);
        String appName = getAppName(referenClazz,env);
        if (StringUtils.hasText(appName)){
            return appUrlMap.get(appName).orElse(null);
        }
        return null;
    }

    /**
     * 获取直连url
     * @param appName 应用名
     * @param env spring容器上下文
     * @return spring容器上下文
     */
    public static String getDirectUrl(String appName , StandardEnvironment env){
        if (StringUtils.hasText(appName)){
            initAppName(env);
            return appUrlMap.get(appName).orElse(null);
        }
        return null;
    }

    private synchronized static void initAppName(StandardEnvironment env){
        if (!pkgAppNameMapInitFlag){
            Map<String, Object> envMap = env.getSystemProperties() ;
            if (!CollectionUtils.isEmpty(envMap)){
                envMap.entrySet().forEach(entry -> {
                    String key = entry.getKey();
                    if (Pattern.matches(apiPackagePattern,key)){
                        String appName = key.substring(appResetPrefix.length() + 1,key.length() - apiPackagePropertyName.length()- 1);
                        pkgAppNameMap.putIfAbsent(entry.getValue().toString(),Optional.of(appName));
                    }else if (Pattern.matches(packageUrlPattern,key)){
                        String pkgName = key.substring("rpc.pkg.reset".length() + 1,key.length() - urlPropertyName.length() - 1);
                        pkgAppNameMap.putIfAbsent(pkgName,Optional.of(pkgName));
                        appUrlMap.putIfAbsent(pkgName,Optional.ofNullable(entry.getValue().toString()));
                    }else if (Pattern.matches(appUrlPattern,key)){
                        String appName = key.substring(appResetPrefix.length() + 1,key.length() - urlPropertyName.length()- 1);
                        appUrlMap.putIfAbsent(appName,Optional.ofNullable(entry.getValue().toString()));
                    }
                });
            }
            pkgAppNameMapInitFlag = true;
        }

    }

    private static String getAppName(Class referenClazz, StandardEnvironment env){
        initAppName(env);
        String appName = getAppNameFromPkgAppNameMap(referenClazz);
        if (StringUtils.hasText(appName)){
            return appName;
        }else {
            RpcInfo rpcInfo = getAppRpcInfo(referenClazz);
            if (rpcInfo != null){
                pkgAppNameMap.putIfAbsent(referenClazz.getPackage().getName(),Optional.ofNullable(rpcInfo.appName()));
                return rpcInfo.appName();
            }
        }
        return null;
    }
    private static String getAppNameFromPkgAppNameMap(Class referenClazz){
        String clazzPackageName = referenClazz.getPackage().getName();
        String pkgName = clazzPackageName;
        while (StringUtils.hasText(pkgName)){
            if (pkgAppNameMap.containsKey(pkgName)){
                Optional<String> optional = pkgAppNameMap.get(pkgName);
                if (!Objects.equals(pkgName,clazzPackageName)){
                    pkgAppNameMap.putIfAbsent(clazzPackageName,optional);
                }
                return optional.orElse(null);
            }else{
                pkgName = PackageUtils.getParentPackageName(pkgName);
            }
        }
        pkgAppNameMap.putIfAbsent(clazzPackageName,Optional.empty());
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

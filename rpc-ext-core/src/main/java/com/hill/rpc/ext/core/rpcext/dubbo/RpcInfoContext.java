package com.hill.rpc.ext.core.rpcext.dubbo;

import com.hill.rpc.ext.core.rpcext.dubbo.annotation.RpcInfo;
import com.hill.rpc.ext.core.utils.AnnotationUtils;

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

    public static String getRpcResetKey (String appName,String parameName){
        return new StringBuilder(appResetPrefix).append(".").append(appName).append(".").append(parameName).toString();
    }
}
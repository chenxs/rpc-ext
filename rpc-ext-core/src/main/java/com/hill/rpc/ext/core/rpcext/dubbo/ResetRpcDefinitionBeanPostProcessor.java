package com.hill.rpc.ext.core.rpcext.dubbo;

import com.alibaba.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import com.alibaba.fastjson.JSONObject;
import com.hill.rpc.ext.core.rpcext.dubbo.annotation.RpcInfo;
import com.hill.rpc.ext.core.rpcext.dubbo.exception.ResetRpcDefinitionErrorException;
import com.hill.rpc.ext.core.utils.AnnotationUtils;
import com.hill.rpc.ext.core.utils.ReflectUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

/**
 * 〈一句话功能简述〉<br>
 * Description: RPC应用级别服务点对点BeanDefinition重置
 * 在加载完spring上下文配置信息后对所有的类型为ReferenceBean的BeanDefinition中直连url进行重置
 *
 * @author hillchen
 * @create 2019/8/25 22:31
 */
public class ResetRpcDefinitionBeanPostProcessor  implements MergedBeanDefinitionPostProcessor,  ApplicationContextAware, PriorityOrdered {
    private ApplicationContext applicationContext;
    private  ConcurrentMap<String, InjectionMetadata> injectionMetadataCache;
    private final String injectionMetadataCacheField = "injectionMetadataCache";
    private final String injectedElementsField = "injectedElements";
    private final String referenceField = "reference";
    private final String urlField = "url";
    private final String timeoutField = "timeOut";
    private final String popFieldName = "field";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.injectionMetadataCache = getCacheInjectionMetadata(applicationContext);
    }
    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        StandardEnvironment env = (StandardEnvironment) applicationContext.getBean(ConfigurableApplicationContext.ENVIRONMENT_BEAN_NAME);
        if (beanType != null){
            String cacheKey = getCacheKey(beanName,beanType);
            InjectionMetadata metadata = injectionMetadataCache.get(cacheKey);
            Collection<InjectionMetadata.InjectedElement> elements = (Collection<InjectionMetadata.InjectedElement>) ReflectUtils.getBeanFieldVal(metadata,injectedElementsField);
            if (elements != null && !elements.isEmpty()){
                elements.stream().forEach(element -> {
                    Object reference = ReflectUtils.getBeanFieldVal(element,referenceField);
                    Field field = (Field)ReflectUtils.getBeanFieldVal(element,popFieldName);
                    Class fieldClazz = field.getType();
                    RpcInfo rpcInfo = RpcInfoContext.getAppRpcInfo(fieldClazz.getName());

                    if (rpcInfo != null ){
                        String appName = rpcInfo.appName();
                        String rpcUrlResetKey = RpcInfoContext.getRpcResetKey(appName, urlField);
                        if (env.containsProperty(rpcUrlResetKey)){
                            String url = env.getProperty(rpcUrlResetKey);
                            AnnotationUtils.setAnnotationFieldVal(reference,urlField,url);
                            AnnotationUtils.setAnnotationFieldVal(reference,timeoutField,1000*60*5);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    private  ConcurrentMap<String, InjectionMetadata> getCacheInjectionMetadata(ApplicationContext applicationContext){
        try{
            ReferenceAnnotationBeanPostProcessor referenceAnnotationBeanPostProcessor = applicationContext.getBean(ReferenceAnnotationBeanPostProcessor.class);
            return (ConcurrentMap<String, InjectionMetadata>) ReflectUtils.getBeanFieldVal(referenceAnnotationBeanPostProcessor,injectionMetadataCacheField);
        }catch (Exception e){
            throw new ResetRpcDefinitionErrorException("get cacheInjectionMetadata error.");
        }
    }

    private String getCacheKey(String beanName,Class clazz){
        return  (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
    }

}
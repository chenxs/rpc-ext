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
 * Description: 纯注解驱动的的dubbo rpc 应用级点对点直连扩展
 *
 * @author hillchen
 * @create 2019/8/25 22:31
 */
public class AnnotationReferenceBeanDefinitionReset implements MergedBeanDefinitionPostProcessor,  ApplicationContextAware, PriorityOrdered {
    private ApplicationContext applicationContext;
    private  ConcurrentMap<String, InjectionMetadata> injectionMetadataCache;
    private StandardEnvironment env;
    private final String injectionMetadataCacheField = "injectionMetadataCache";
    private final String injectedElementsField = "injectedElements";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        StandardEnvironment env = getEnv();
        if (beanType != null){
            String cacheKey = getCacheKey(beanName,beanType);
            InjectionMetadata metadata = getCacheInjectionMetadata().get(cacheKey);
            Collection<InjectionMetadata.InjectedElement> elements = (Collection<InjectionMetadata.InjectedElement>) ReflectUtils.getBeanFieldVal(metadata,injectedElementsField);
            if (elements != null && !elements.isEmpty()){
                elements.stream().forEach(element -> {
                    RpcInfoContext.resetToDirect(env,element);
                });
            }
        }
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    private  ConcurrentMap<String, InjectionMetadata> getCacheInjectionMetadata(){
        if (injectionMetadataCache == null){
            try{
                ReferenceAnnotationBeanPostProcessor referenceAnnotationBeanPostProcessor = applicationContext.getBean(ReferenceAnnotationBeanPostProcessor.class);
                injectionMetadataCache =  (ConcurrentMap<String, InjectionMetadata>) ReflectUtils.getBeanFieldVal(referenceAnnotationBeanPostProcessor,injectionMetadataCacheField);
            }catch (Exception e){
                throw new ResetRpcDefinitionErrorException("get cacheInjectionMetadata error.");
            }
        }
        return injectionMetadataCache;

    }

    private String getCacheKey(String beanName,Class clazz){
        return  (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
    }


    private StandardEnvironment getEnv(){
        if (env == null){
            env = (StandardEnvironment)applicationContext.getBean(ConfigurableApplicationContext.ENVIRONMENT_BEAN_NAME);
        }
        return env;
    }
}
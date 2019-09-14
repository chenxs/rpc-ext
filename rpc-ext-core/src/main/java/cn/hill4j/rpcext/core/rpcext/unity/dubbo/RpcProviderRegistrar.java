package cn.hill4j.rpcext.core.rpcext.unity.dubbo;

import cn.hill4j.rpcext.core.rpcext.unity.dubbo.annotation.EnableRpcProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.*;

/**
 * 2019/9/11 15:54<br>
 * Description: 服务暴露bean初始化处理器
 *
 * @author hillchen
 */
public class RpcProviderRegistrar implements ImportBeanDefinitionRegistrar {
    private final String rpcProviderExportBeanName = "rpcProviderExportPostProcessor";
    private final String rpcProviderLoadBeanName = "rpcProviderLoadPostProcessor";
    /**
     * 校验上下文中是否有依赖dubbo
     */
    private final String dubboLoaderCheckClassName = "com.alibaba.dubbo.config.spring.schema.DubboNamespaceHandler";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {


        if(isLoader(dubboLoaderCheckClassName)){
            if (!registry.containsBeanDefinition(rpcProviderExportBeanName)){
                Set<String> toProviderAppNames = getToProviderAppNames(importingClassMetadata);
                BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(RpcProviderExportPostProcessor.class).getBeanDefinition();
                beanDefinition.getPropertyValues().add("toProviderAppNames", toProviderAppNames);
                registry.registerBeanDefinition(rpcProviderExportBeanName,beanDefinition);
            }

            if (!registry.containsBeanDefinition(rpcProviderLoadBeanName)){
                BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(RpcProviderLoadPostProcessor.class).getBeanDefinition();
                registry.registerBeanDefinition(rpcProviderLoadBeanName,beanDefinition);
            }

        }

    }

    private Set<String> getToProviderAppNames(AnnotationMetadata importingClassMetadata){
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(EnableRpcProvider.class.getName()));
        String[] appNames = attributes.getStringArray("value");
        return new HashSet<>(Arrays.asList(appNames));
    }

    private boolean isLoader(String className){
        ClassLoader classLoader = RpcProviderRegistrar.class.getClassLoader();
        try {
            classLoader.loadClass(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}

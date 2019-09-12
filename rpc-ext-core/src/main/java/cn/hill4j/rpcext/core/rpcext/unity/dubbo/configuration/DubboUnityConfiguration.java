package cn.hill4j.rpcext.core.rpcext.unity.dubbo.configuration;

import cn.hill4j.rpcext.core.processor.ProcessorResort;
import cn.hill4j.rpcext.core.rpcext.direct.dubbo.AnnotationBeanDefinitionReset;
import cn.hill4j.rpcext.core.rpcext.direct.dubbo.AnnotationReferenceBeanDefinitionReset;
import cn.hill4j.rpcext.core.rpcext.direct.dubbo.XmlReferenceBeanDefinitionReset;
import cn.hill4j.rpcext.core.rpcext.unity.dubbo.RpcProviderExportPostProcessor;
import cn.hill4j.rpcext.core.rpcext.unity.dubbo.RpcProviderLoadPostProcessor;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import com.alibaba.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import com.alibaba.dubbo.config.spring.schema.DubboNamespaceHandler;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 2019/8/26 16:40 <br>
 * Description: dubbo统一服务端客户端配置信息扩展点自动装载类
 *
 * @author hillchen
 */
@Configuration
@ConditionalOnClass({DubboNamespaceHandler.class})
public class DubboUnityConfiguration {
    /**
     * dubbo统一服务端服务暴露扩展类
     * @return 构造dubbo统一服务端服务暴露扩展类RpcProviderExportPostProcessor
     */
    @Bean
    public RpcProviderExportPostProcessor rpcProviderExportPostProcessor(){
        return new RpcProviderExportPostProcessor();
    }

    /**
     *
     * @return 加载带有RpcApi的注解的所有接口
     */
    @Bean
    public RpcProviderLoadPostProcessor rpcProviderLoadPostProcessor(){
        return new RpcProviderLoadPostProcessor();
    }
}

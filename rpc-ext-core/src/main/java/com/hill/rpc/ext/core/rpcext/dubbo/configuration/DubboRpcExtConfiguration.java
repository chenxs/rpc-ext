package com.hill.rpc.ext.core.rpcext.dubbo.configuration;

import com.alibaba.dubbo.config.spring.AnnotationBean;
import com.alibaba.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import com.alibaba.dubbo.config.spring.schema.DubboNamespaceHandler;
import com.hill.rpc.ext.core.processor.ProcessorResort;
import com.hill.rpc.ext.core.rpcext.dubbo.AnnotationBeanDefinitionReset;
import com.hill.rpc.ext.core.rpcext.dubbo.XmlReferenceBeanDefinitionReset;
import com.hill.rpc.ext.core.rpcext.dubbo.AnnotationReferenceBeanDefinitionReset;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 〈一句话功能简述〉<br>
 * Description: RPC扩展功能自动装置bean
 *
 * @author hillchen
 * @create 2019/8/26 16:40
 */
@Configuration
@ConditionalOnClass({DubboNamespaceHandler.class})
public class DubboRpcExtConfiguration {
    @Bean
    public XmlReferenceBeanDefinitionReset xmlReferenceBeanDefinitionReset(){
        return new XmlReferenceBeanDefinitionReset();
    }

    @Bean
    @ConditionalOnClass({ReferenceAnnotationBeanPostProcessor.class})
    @ConditionalOnBean(name = {"referenceAnnotationBeanPostProcessor"})
    public AnnotationReferenceBeanDefinitionReset annotationReferenceBeanDefinitionReset(){
        return new AnnotationReferenceBeanDefinitionReset();
    }

    @Bean
    @ConditionalOnClass({ReferenceAnnotationBeanPostProcessor.class})
    @ConditionalOnBean(name = {"referenceAnnotationBeanPostProcessor"})
    public ProcessorResort dubboReferencePostProcessorResort(){
        return new ProcessorResort(){
            @Override
            public BeanPostProcessor getBeforeBeanProcessor(ApplicationContext applicationContext) {
                return applicationContext.getBean(ReferenceAnnotationBeanPostProcessor.class,"referenceAnnotationBeanPostProcessor");
            }

            @Override
            public BeanPostProcessor getAfterBeanProcessor(ApplicationContext applicationContext) {
                return annotationReferenceBeanDefinitionReset();
            }

            @Override
            public boolean afterFixed() {
                return true;
            }
        };
    }


    @Bean
    @ConditionalOnBean({AnnotationBean.class})
    public AnnotationBeanDefinitionReset annotationBeanDefinitionReset(){
        return new AnnotationBeanDefinitionReset();
    }

    @Bean
    @ConditionalOnBean({AnnotationBean.class})
    public ProcessorResort dubboAnnotationBeanResort(){
        return new ProcessorResort(){
            @Override
            public BeanPostProcessor getBeforeBeanProcessor(ApplicationContext applicationContext) {
                return annotationBeanDefinitionReset();
                }

            @Override
            public BeanPostProcessor getAfterBeanProcessor(ApplicationContext applicationContext) {
                return applicationContext.getBean(AnnotationBean.class,"referenceAnnotationBeanPostProcessor");
            }

            @Override
            public boolean afterFixed() {
                return false;
            }
        };
    }
}
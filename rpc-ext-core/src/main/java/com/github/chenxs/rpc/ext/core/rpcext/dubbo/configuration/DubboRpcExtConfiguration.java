package com.github.chenxs.rpc.ext.core.rpcext.dubbo.configuration;

import com.alibaba.dubbo.config.spring.AnnotationBean;
import com.alibaba.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import com.alibaba.dubbo.config.spring.schema.DubboNamespaceHandler;
import com.github.chenxs.rpc.ext.core.processor.ProcessorResort;
import com.github.chenxs.rpc.ext.core.rpcext.dubbo.AnnotationBeanDefinitionReset;
import com.github.chenxs.rpc.ext.core.rpcext.dubbo.AnnotationReferenceBeanDefinitionReset;
import com.github.chenxs.rpc.ext.core.rpcext.dubbo.XmlReferenceBeanDefinitionReset;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 〈一句话功能简述〉<br>
 * Description: dubbo的RPC扩展功能自动装置bean
 *
 * @author hillchen
 * @create 2019/8/26 16:40
 */
@Configuration
@ConditionalOnClass({DubboNamespaceHandler.class})
public class DubboRpcExtConfiguration {
    /**
     * xml配置文件驱动的ReferenceBeanDefinition的dubbo rpc 应用级点对点直连扩展
     * @return
     */
    @Bean
    public XmlReferenceBeanDefinitionReset xmlReferenceBeanDefinitionReset(){
        return new XmlReferenceBeanDefinitionReset();
    }

    /**
     * 纯注解驱动的的dubbo rpc 应用级点对点直连扩展
     * @return
     */
    @Bean
    @ConditionalOnClass({ReferenceAnnotationBeanPostProcessor.class})
    @ConditionalOnBean(name = {"referenceAnnotationBeanPostProcessor"})
    public AnnotationReferenceBeanDefinitionReset annotationReferenceBeanDefinitionReset(){
        return new AnnotationReferenceBeanDefinitionReset();
    }

    /**
     * 纯注解驱动的的dubbo rpc 应用级点对点直连扩展
     * 必须要在dubbo自定义的ReferenceAnnotationBeanPostProcessor之后执行,这边手动调整顺序以保证执行顺序的确定性
     * @return
     */
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


    /**
     * AnnotationBean驱动的的dubbo rpc 应用级点对点直连扩展
     * @return
     */
    @Bean
    @ConditionalOnBean({AnnotationBean.class})
    public AnnotationBeanDefinitionReset annotationBeanDefinitionReset(){
        return new AnnotationBeanDefinitionReset();
    }

    /**
     * AnnotationBean驱动的的dubbo rpc 应用级点对点直连扩展bean必须在dubbo的AnnotationBean执行前现执行
     * @return
     */
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
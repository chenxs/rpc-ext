package cn.hill4j.rpcext.core.rpcext.direct.dubbo.configuration;

import cn.hill4j.rpcext.core.processor.ProcessorResort;
import cn.hill4j.rpcext.core.rpcext.direct.dubbo.AnnotationBeanDefinitionReset;
import cn.hill4j.rpcext.core.rpcext.direct.dubbo.AnnotationReferenceBeanDefinitionReset;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import com.alibaba.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import com.alibaba.dubbo.config.spring.schema.DubboNamespaceHandler;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cn.hill4j.rpcext.core.rpcext.direct.dubbo.XmlReferenceBeanDefinitionReset;

/**
 * 2019/8/26 16:40 <br>
 * Description: dubbo的RPC扩展功能自动装置bean
 *
 * @author hillchen
 */
@Configuration
@ConditionalOnClass({DubboNamespaceHandler.class})
public class DubboRpcExtConfiguration {
    /**
     * xml配置文件驱动的ReferenceBeanDefinition的dubbo rpc 应用级点对点直连扩展
     * @return 构造XmlReferenceBeanDefinitionReset bean
     */
    @Bean
    public XmlReferenceBeanDefinitionReset xmlReferenceBeanDefinitionReset(){
        return new XmlReferenceBeanDefinitionReset();
    }

    /**
     * 纯注解驱动的的dubbo rpc 应用级点对点直连扩展
     * @return 构造AnnotationReferenceBeanDefinitionReset bean
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
     * @return 构造 dubboReferencePostProcessorResort bean
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
     * @return 构造 AnnotationBeanDefinitionReset bean
     */
    @Bean
    @ConditionalOnBean({AnnotationBean.class})
    public AnnotationBeanDefinitionReset annotationBeanDefinitionReset(){
        return new AnnotationBeanDefinitionReset();
    }

    /**
     * AnnotationBean驱动的的dubbo rpc 应用级点对点直连扩展bean必须在dubbo的AnnotationBean执行前现执行
     * @return 构造dubboAnnotationBeanResort bean
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
                return applicationContext.getBean(AnnotationBean.class);
            }

            @Override
            public boolean afterFixed() {
                return false;
            }
        };
    }
}

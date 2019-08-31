package com.github.chenxs.rpc.ext.core.rpcext.dubbo;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.StandardEnvironment;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 〈一句话功能简述〉<br>
 * Description: xml配置文件驱动的ReferenceBeanDefinition的dubbo rpc 应用级点对点直连扩展
 * 在加载完spring上下文配置信息后对所有的类型为ReferenceBean的BeanDefinition中直连url进行重置
 *
 * @author hillchen
 * @create 2019/8/25 22:31
 */
public class XmlReferenceBeanDefinitionReset implements BeanFactoryPostProcessor, ApplicationContextAware, Ordered {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        StandardEnvironment env = (StandardEnvironment) beanFactory.getSingleton(ConfigurableApplicationContext.ENVIRONMENT_BEAN_NAME);
        String[] beanNames = beanFactory.getBeanDefinitionNames();

        Stream.of(beanNames)
                .map(beanName -> beanFactory.getBeanDefinition(beanName))
                .forEach(beanDefinition -> {
                    if (Objects.equals(ReferenceBean.class.getName(),beanDefinition.getBeanClassName())) {
                        MutablePropertyValues mutablePropertyValues = beanDefinition.getPropertyValues();
                        RpcInfoContext.resetToDirect(env,mutablePropertyValues);
                    }
                });
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        System.out.println(applicationContext);
    }

}
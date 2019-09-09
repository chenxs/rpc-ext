package cn.hill4j.rpcext.demo.dubbo.client.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * 〈一句话功能简述〉<br>
 * Description: TestBeanDefinitionRegistryPostProcessor
 *
 * @author hillchen
 * @create 2019/9/5 20:36
 */
public class TestBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    private Logger logger = LoggerFactory.getLogger(TestBeanDefinitionRegistryPostProcessor.class);
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        logger.error("TestBeanDefinitionRegistryPostProcessor:postProcessBeanDefinitionRegistry");
        registry.registerBeanDefinition("testParam",new RootBeanDefinition(TestParam.class));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.error("TestBeanDefinitionRegistryPostProcessor:postProcessBeanFactory");
    }
}

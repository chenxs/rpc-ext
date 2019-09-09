package cn.hill4j.rpcext.demo.dubbo.client.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.util.Objects;

/**
 * 〈一句话功能简述〉<br>
 * Description: testMergedBeanDefinitionPostProcessor
 *
 * @author hillchen
 * @create 2019/9/5 16:11
 */
public class TestMergedBeanDefinitionPostProcessor implements MergedBeanDefinitionPostProcessor {

    private Logger logger = LoggerFactory.getLogger(TestMergedBeanDefinitionPostProcessor.class);
    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        if (Objects.equals(beanType,TestBean.class) ||  Objects.equals(beanType,TestBean2.class) ) {
            logger.error("beanName:" + beanName + ":TestMergedBeanDefinitionPostProcessor.postProcessMergedBeanDefinition");
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TestBean ||  bean instanceof TestBean2) {
            logger.error("beanName:" + beanName + ":TestMergedBeanDefinitionPostProcessor.postProcessBeforeInitialization");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TestBean ||  bean instanceof TestBean2) {
            logger.error("beanName:" + beanName + ":TestMergedBeanDefinitionPostProcessor.postProcessAfterInitialization");
        }
        return bean;
    }
}

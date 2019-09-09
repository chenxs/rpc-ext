package cn.hill4j.rpcext.demo.dubbo.client.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;

/**
 * 〈一句话功能简述〉<br>
 * Description: TestSmartInstantiationAwareBeanPostProcessor
 *
 * @author hillchen
 * @create 2019/9/5 16:18
 */
public class TestSmartInstantiationAwareBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {
    private Logger logger = LoggerFactory.getLogger(TestSmartInstantiationAwareBeanPostProcessor.class);

    @Override
    public Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
        logger.error("beanName:" + beanName + ":TestSmartInstantiationAwareBeanPostProcessor.predictBeanType");
        return beanClass;
    }

    @Override
    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
        logger.error("beanName:" + beanName + ":TestSmartInstantiationAwareBeanPostProcessor.determineCandidateConstructors");
        return new Constructor[0];
    }

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        logger.error("beanName:" + beanName + ":TestSmartInstantiationAwareBeanPostProcessor.getEarlyBeanReference");
        return bean;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        logger.error("beanName:" + beanName + ":TestSmartInstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation");
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        logger.error("beanName:" + beanName + ":TestSmartInstantiationAwareBeanPostProcessor.postProcessAfterInstantiation");
        return false;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        logger.error("beanName:" + beanName + ":TestSmartInstantiationAwareBeanPostProcessor.postProcessPropertyValues");
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        logger.error("beanName:" + beanName + ":TestSmartInstantiationAwareBeanPostProcessor.postProcessBeforeInitialization");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        logger.error("beanName:" + beanName + ":TestSmartInstantiationAwareBeanPostProcessor.postProcessAfterInitialization");
        return bean;
    }
}

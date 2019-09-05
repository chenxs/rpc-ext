package com.github.chenxs.rpc.ext.demo.dubbo.client.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

import java.beans.PropertyDescriptor;

/**
 * 〈一句话功能简述〉<br>
 * Description: TestInstantiationAwareBeanPostProcessor
 *
 * @author hillchen
 * @create 2019/9/5 16:15
 */
public class TestInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    private Logger logger = LoggerFactory.getLogger(TestInstantiationAwareBeanPostProcessor.class);
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        logger.error("beanName:" + beanName + ":TestInstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation");
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        logger.error("beanName:" + beanName + ":TestInstantiationAwareBeanPostProcessor.postProcessAfterInstantiation");
        return false;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        logger.error("beanName:" + beanName + ":TestInstantiationAwareBeanPostProcessor.postProcessPropertyValues");
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        logger.error("beanName:" + beanName + ":TestInstantiationAwareBeanPostProcessor.postProcessBeforeInitialization");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        logger.error("beanName:" + beanName + ":TestInstantiationAwareBeanPostProcessor.postProcessAfterInitialization");
        return bean;
    }
}

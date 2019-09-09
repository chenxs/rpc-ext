package cn.hill4j.rpcext.demo.dubbo.client.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.beans.PropertyDescriptor;
import java.util.Objects;

/**
 * 〈一句话功能简述〉<br>
 * Description: TestInstantiationAwareBeanPostProcessor
 *
 * @author hillchen
 * @create 2019/9/5 16:15
 */
public class TestInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor , ApplicationContextAware {
    private ApplicationContext applicationContext;
    private Logger logger = LoggerFactory.getLogger(TestInstantiationAwareBeanPostProcessor.class);
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (Objects.equals(beanClass,TestParam.class) ){
            return new TestParam();
        }
        if (Objects.equals(beanClass,  TestBean.class) || Objects.equals(beanClass,  TestBean2.class)) {
            try{
                applicationContext.getBean(beanName);
            }catch (Exception e){
                logger.error(beanName + "：has not create");
            }

            logger.error("beanName:" + beanName +  ":TestInstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation");
        }
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if (bean instanceof TestBean || bean instanceof TestBean2 ) {
            applicationContext.getBean(beanName);
            logger.error("beanName:" + beanName  +  ":TestInstantiationAwareBeanPostProcessor.postProcessAfterInstantiation");
        }
        return true;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        if (bean instanceof TestBean || bean instanceof TestBean2) {
            logger.error("beanName:" + beanName +  ":TestInstantiationAwareBeanPostProcessor.postProcessPropertyValues");
        }
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TestBean || bean instanceof TestBean2) {
            applicationContext.getBean(beanName);
            logger.error("beanName:" + beanName + ":TestInstantiationAwareBeanPostProcessor.postProcessBeforeInitialization");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TestBean ) {
            logger.error("beanName:" + beanName +  ":TestInstantiationAwareBeanPostProcessor.postProcessAfterInitialization");
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

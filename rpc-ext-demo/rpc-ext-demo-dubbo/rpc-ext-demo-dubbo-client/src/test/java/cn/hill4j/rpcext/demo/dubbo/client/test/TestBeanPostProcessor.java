package cn.hill4j.rpcext.demo.dubbo.client.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 〈一句话功能简述〉<br>
 * Description: TestSmartInstantiationAwareBeanPostProcessor
 *
 * @author hillchen
 * @create 2019/9/5 16:18
 */
public class TestBeanPostProcessor implements BeanPostProcessor {
    private Logger logger = LoggerFactory.getLogger(TestBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TestBean || bean instanceof TestBean2){
            logger.error("beanName:" + beanName + ":TestBeanPostProcessor.postProcessBeforeInitialization");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TestBean || bean instanceof TestBean2) {
            logger.error("beanName:" + beanName + ":TestBeanPostProcessor.postProcessAfterInitialization");
        }
        return bean;
    }
}

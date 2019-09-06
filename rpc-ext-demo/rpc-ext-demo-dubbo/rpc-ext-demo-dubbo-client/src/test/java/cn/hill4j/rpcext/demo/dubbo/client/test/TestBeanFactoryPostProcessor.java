package cn.hill4j.rpcext.demo.dubbo.client.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * 〈一句话功能简述〉<br>
 * Description: 测试beanFactoryPostProcessor
 *
 * @author hillchen
 * @create 2019/9/5 16:06
 */
public class TestBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    private Logger logger = LoggerFactory.getLogger(TestBeanFactoryPostProcessor.class);
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.error("TestBeanFactoryPostProcessor");
    }
}

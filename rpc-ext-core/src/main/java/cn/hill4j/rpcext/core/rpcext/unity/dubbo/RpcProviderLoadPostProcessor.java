package cn.hill4j.rpcext.core.rpcext.unity.dubbo;

import cn.hill4j.rpcext.core.rpcext.dubbo.annotation.RpcApi;
import cn.hill4j.rpcext.core.utils.ReflectUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

/**
 * 2019/9/11 15:54<br>
 * Description: 服务暴露bean初始化处理器
 *
 * @author hillchen
 */
public class RpcProviderLoadPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanNamesForAnnotation(RpcApi.class);
        System.out.println(JSONObject.toJSONString(beanNames));
    }
}

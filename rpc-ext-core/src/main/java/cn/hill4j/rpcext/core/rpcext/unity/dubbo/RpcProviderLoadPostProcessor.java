package cn.hill4j.rpcext.core.rpcext.unity.dubbo;

import cn.hill4j.rpcext.core.rpcext.direct.dubbo.RpcInfoContext;
import cn.hill4j.rpcext.core.rpcext.dubbo.annotation.RpcApi;
import cn.hill4j.rpcext.core.utils.ReflectUtils;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.alibaba.dubbo.config.spring.ServiceBean;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.env.StandardEnvironment;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 2019/9/11 15:54<br>
 * Description: 服务暴露bean初始化处理器
 *
 * @author hillchen
 */
public class RpcProviderLoadPostProcessor implements BeanFactoryPostProcessor {
    private static final Set<Class> XML_EXPORT_PROVIDER_INTERFACES = new HashSet<>();
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        Stream.of(beanNames)
                .map(beanName -> beanFactory.getBeanDefinition(beanName))
                .forEach(beanDefinition -> {
                    if (Objects.equals(ServiceBean.class.getName(),beanDefinition.getBeanClassName())) {
                        MutablePropertyValues mutablePropertyValues = beanDefinition.getPropertyValues();
                        Object ref = mutablePropertyValues.get("interfaceClass");
                        if (ref != null){
                            XML_EXPORT_PROVIDER_INTERFACES.add((Class)ref);
                        }
                    }
                });
    }
    public static boolean hasExported(Class interfaceClass){
        return XML_EXPORT_PROVIDER_INTERFACES.contains(interfaceClass);
    }
}

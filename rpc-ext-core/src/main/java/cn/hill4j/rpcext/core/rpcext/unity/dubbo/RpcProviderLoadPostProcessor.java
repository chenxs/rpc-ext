package cn.hill4j.rpcext.core.rpcext.unity.dubbo;

import com.alibaba.dubbo.config.spring.ServiceBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

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

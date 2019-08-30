package com.hill.rpc.ext.core.rpcext.dubbo;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.hill.rpc.ext.core.rpcext.dubbo.annotation.RpcInfo;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.StandardEnvironment;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 〈一句话功能简述〉<br>
 * Description: RPC应用级别服务点对点BeanDefinition重置
 * 在加载完spring上下文配置信息后对所有的类型为ReferenceBean的BeanDefinition中直连url进行重置
 *
 * @author hillchen
 * @create 2019/8/25 22:31
 */
public class ResetRpcDefinition implements BeanFactoryPostProcessor, ApplicationContextAware, Ordered {
    private final String interfacePropertyName = "interface";
    private final String urlPropertyName = "url";
    private final String timeoutPropertyName = "timeout";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        StandardEnvironment env = (StandardEnvironment) beanFactory.getSingleton(ConfigurableApplicationContext.ENVIRONMENT_BEAN_NAME);
        String[] beanNames = beanFactory.getBeanDefinitionNames();

        Stream.of(beanNames)
                .map(beanName -> beanFactory.getBeanDefinition(beanName))
                .forEach(beanDefinition -> {
                    if (Objects.equals(ReferenceBean.class.getName(),beanDefinition.getBeanClassName())) {
                        MutablePropertyValues mutablePropertyValues = beanDefinition.getPropertyValues();

                        String interfaceName = mutablePropertyValues.getPropertyValue(interfacePropertyName).getValue().toString();
                        RpcInfo rpcInfo = RpcInfoContext.getAppRpcInfo(interfaceName);
                        if (rpcInfo != null ){
                            String appName = rpcInfo.appName();
                            String rpcUrlResetKey = RpcInfoContext.getRpcResetKey(appName,urlPropertyName);
                            if (env.containsProperty(rpcUrlResetKey)){
                                String url = env.getProperty(rpcUrlResetKey);
                                mutablePropertyValues.removePropertyValue(urlPropertyName);
                                mutablePropertyValues.add(urlPropertyName,url);

                                // 当存在点对点直连时，所在点对点服务的超时时间为5分钟
                                mutablePropertyValues.removePropertyValue(timeoutPropertyName);
                                mutablePropertyValues.add(timeoutPropertyName,1000*60*5);
                            }
                        }
                    }
                });
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        System.out.println(applicationContext);
    }

}
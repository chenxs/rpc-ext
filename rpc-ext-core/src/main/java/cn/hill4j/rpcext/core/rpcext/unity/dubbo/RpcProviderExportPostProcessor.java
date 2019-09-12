package cn.hill4j.rpcext.core.rpcext.unity.dubbo;

import cn.hill4j.rpcext.core.rpcext.dubbo.annotation.RpcApi;
import cn.hill4j.rpcext.core.utils.AnnotationUtils;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.alibaba.dubbo.config.*;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.config.spring.ServiceBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 2019/9/11 15:54<br>
 * Description: 服务暴露bean初始化处理器
 *
 * @author hillchen
 */
public class RpcProviderExportPostProcessor  implements BeanPostProcessor , DisposableBean , ApplicationContextAware {
    private ApplicationContext applicationContext;
    private final Set<ServiceConfig<?>> serviceConfigs = new ConcurrentHashSet<ServiceConfig<?>>();
    @Override
    public void destroy() throws Exception {

    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClazz = bean.getClass();
        Class[] interfaces = beanClazz.getInterfaces();
        if (interfaces !=  null){
            for (Class interfaceClazz : interfaces){
                if (interfaceClazz.isAnnotationPresent(RpcApi.class)){
                    RpcApi rpcApi = (RpcApi) interfaceClazz.getAnnotation(RpcApi.class);
                    Service service = AnnotationUtils.transformToOther(rpcApi,Service.class);
                    exportService(bean,service,interfaceClazz);
                }
            }
        }
        // 判断bean是否需要暴露为服务提供者
        // 封装服务bean的dubbo Service definition信息
        // 将服务暴露到注册中心
        return bean;
    }

    private boolean needExport (Object bean, String beanName){

        // bean 是否为RpcApi实现
        // AnnotationUtils.recursionGet(bea)
        // bean 是否已经被其他方式暴露过

        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Object exportService(Object bean, Service service,Class interfaceClass)
            throws BeansException {
        if (service != null) {
            ServiceBean<Object> serviceConfig = new ServiceBean<Object>(service);
            serviceConfig.setRef(bean);
            serviceConfig.setInterface(interfaceClass);
            if (applicationContext != null) {
                serviceConfig.setApplicationContext(applicationContext);
                if (service.registry() != null && service.registry().length > 0) {
                    List<RegistryConfig> registryConfigs = new ArrayList<RegistryConfig>();
                    for (String registryId : service.registry()) {
                        if (registryId != null && registryId.length() > 0) {
                            registryConfigs.add(applicationContext.getBean(registryId, RegistryConfig.class));
                        }
                    }
                    serviceConfig.setRegistries(registryConfigs);
                }
                if (service.provider() != null && service.provider().length() > 0) {
                    serviceConfig.setProvider(applicationContext.getBean(service.provider(), ProviderConfig.class));
                }
                if (service.monitor() != null && service.monitor().length() > 0) {
                    serviceConfig.setMonitor( applicationContext.getBean(service.monitor(), MonitorConfig.class));
                }
                if (service.application() != null && service.application().length() > 0) {
                    serviceConfig.setApplication( applicationContext.getBean(service.application(), ApplicationConfig.class));
                }
                if (service.module() != null && service.module().length() > 0) {
                    serviceConfig.setModule( applicationContext.getBean(service.module(), ModuleConfig.class));
                }
                if (service.provider() != null && service.provider().length() > 0) {
                    serviceConfig.setProvider( applicationContext.getBean(service.provider(), ProviderConfig.class));
                } else {

                }
                if (service.protocol() != null && service.protocol().length > 0) {
                    List<ProtocolConfig> protocolConfigs = new ArrayList<ProtocolConfig>();
                    for (String protocolId : service.protocol()) {
                        if (protocolId != null && protocolId.length() > 0) {
                            protocolConfigs.add( applicationContext.getBean(protocolId, ProtocolConfig.class));
                        }
                    }
                    serviceConfig.setProtocols(protocolConfigs);
                }
                try {
                    serviceConfig.afterPropertiesSet();
                } catch (RuntimeException e) {
                    throw  e;
                } catch (Exception e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }
            }
            serviceConfigs.add(serviceConfig);
            serviceConfig.export();
        }
        return bean;
    }

}

package cn.hill4j.rpcext.core.rpcext.unity.dubbo;

import cn.hill4j.rpcext.core.rpcext.direct.dubbo.RpcInfoContext;
import cn.hill4j.rpcext.core.rpcext.dubbo.annotation.RpcApi;
import cn.hill4j.rpcext.core.rpcext.dubbo.annotation.RpcInfo;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 2019/9/11 15:54<br>
 * Description: 服务暴露bean初始化处理器
 *
 * @author hillchen
 */
public class RpcProviderExportPostProcessor  implements BeanPostProcessor , DisposableBean , ApplicationContextAware {
    private Set<String> toProviderAppNames = new HashSet<>();
    private ApplicationContext applicationContext;
    private final Set<ServiceConfig<?>> serviceConfigs = new ConcurrentHashSet<ServiceConfig<?>>();
    @Override
    public void destroy() throws Exception {

    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        exportToProvider(bean);
        return bean;
    }

    private void exportToProvider(Object bean){
        Class beanClazz = bean.getClass();
        // 通过dubbo service注解已经暴露过了
        if (beanClazz.isAnnotationPresent(Service.class)){
            return;
        }
        Class[] interfaces = beanClazz.getInterfaces();
        if (interfaces ==  null){
            return;
        }
        for (Class interfaceClazz : interfaces){
            if (interfaceClazz.isAnnotationPresent(RpcApi.class)
                && !RpcProviderLoadPostProcessor.hasExported(interfaceClazz)  // 通过xml配置文件暴露过
            ){
                RpcInfo rpcInfo = RpcInfoContext.getAppRpcInfo(interfaceClazz);
                if (needExport(rpcInfo)){
                    RpcApi rpcApi = (RpcApi) interfaceClazz.getAnnotation(RpcApi.class);
                    Service service = AnnotationUtils.transformToOther(rpcApi,Service.class);
                    exportService(bean,service,interfaceClazz);
                }
            }
        }
    }

    private boolean needExport (RpcInfo rpcInfo){
        if (toProviderAppNames.isEmpty()){
            return true;
        }
        return rpcInfo != null && toProviderAppNames.contains(rpcInfo.appName());
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

    public void setToProviderAppNames(Set<String> toProviderAppNames) {
        if (toProviderAppNames != null){
            this.toProviderAppNames.addAll(toProviderAppNames);
        }
    }
}

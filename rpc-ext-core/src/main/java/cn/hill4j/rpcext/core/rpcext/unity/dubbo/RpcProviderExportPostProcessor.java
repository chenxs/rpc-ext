package cn.hill4j.rpcext.core.rpcext.unity.dubbo;

import cn.hill4j.rpcext.core.rpcext.direct.dubbo.RpcInfoContext;
import cn.hill4j.rpcext.core.rpcext.dubbo.annotation.RpcApi;
import cn.hill4j.rpcext.core.rpcext.dubbo.annotation.RpcInfo;
import cn.hill4j.rpcext.core.rpcext.unity.dubbo.exception.RpcProviderExportException;
import cn.hill4j.rpcext.core.utils.AnnotationUtils;
import cn.hill4j.rpcext.core.utils.PackageUtils;
import cn.hill4j.rpcext.core.utils.ReflectUtils;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.alibaba.dubbo.config.*;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import com.alibaba.dubbo.config.spring.ServiceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    private Logger logger = LoggerFactory.getLogger(RpcProviderExportPostProcessor.class);
    private Set<String> toProviderAppNames = new HashSet<>();
    private Set<String> basePackages = new HashSet<>();
    private Set<String> excludedAppNames = new HashSet<>();
    private Set<String> excludedPackages = new HashSet<>();

    private boolean dubboAnnotationBeanExport = false;
    private Method dubboServiceBeanMatchMethod = null;
    private AnnotationBean annotationBean = null;

    private ApplicationContext applicationContext;
    private final Set<ServiceConfig<?>> serviceConfigs = new ConcurrentHashSet<ServiceConfig<?>>();
    @Override
    public void destroy() throws Exception {
        for (ServiceConfig<?> serviceConfig : serviceConfigs) {
            try {
                serviceConfig.unexport();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        exportToProvider(bean);
        return bean;
    }

    private void exportToProvider(Object bean){
        Class beanClazz = bean.getClass();
        Set<Class> interfaces = ReflectUtils.getAllInterfaces(beanClazz);
        if (CollectionUtils.isEmpty(interfaces)){
            return;
        }
        Set<Class> exportInterfaceClazzs = initExportedInterfaces(bean,beanClazz, interfaces);

        for (Class interfaceClazz : interfaces){
            if (interfaceClazz.isAnnotationPresent(RpcApi.class)){
                if (!hasExported(interfaceClazz,exportInterfaceClazzs)){
                    exportRpcProvider(bean, interfaceClazz);
                }
                exportInterfaceClazzs.add(interfaceClazz);
            }
        }
    }

    /**
     * 初始化已经导出为服务提供者的接口列表
     * @param beanClazz 当前bean类
     * @param interfaces bean实现的接口列表
     * @return 已经导出为服务提供者的接口列表
     */
    private Set<Class> initExportedInterfaces(Object bean,Class beanClazz, Set<Class> interfaces) {
        Set<Class> exportInterfaceClazzs = new HashSet<>();
        Class  dubboServiceInterface = getDubboServiceInterface(bean,beanClazz);
        if (dubboServiceInterface != null){
            exportInterfaceClazzs.add(dubboServiceInterface);
        }
        for (Class interfaceClazz : interfaces){
            if (RpcProviderLoadPostProcessor.hasExported(interfaceClazz)){
                exportInterfaceClazzs.add(interfaceClazz);
            }
        }
        return exportInterfaceClazzs;
    }

    /**
     * 将bean对象导出为指定接口的rpc服务类
     * @param bean 服务bean
     * @param interfaceClazz rpc接口
     */
    private void exportRpcProvider(Object bean, Class interfaceClazz) {
        RpcInfo rpcInfo = RpcInfoContext.getAppRpcInfo(interfaceClazz);
        if (needExportProvider(rpcInfo,interfaceClazz)){
            RpcApi rpcApi = (RpcApi) interfaceClazz.getAnnotation(RpcApi.class);
            Service service = AnnotationUtils.transformToOther(rpcApi,Service.class);
            exportService(bean,service,interfaceClazz);
        }
    }

    /**
     * 判断自定rpc接口是否已经导出过服务
     * @param interfaceClazz rpc接口
     * @param exportInterfaceClazzs 已导出rpc接口
     * @return
     */
    private boolean hasExported (@NotNull Class interfaceClazz, @NotNull Set<Class> exportInterfaceClazzs ){
        for (Class clazz: exportInterfaceClazzs){
            if (interfaceClazz.isAssignableFrom(clazz)){
                return true;
            }
        }
        return false;
    }

    private boolean needExportProvider (RpcInfo rpcInfo,@NotNull Class interfaceClazz){
        Set<String> parentPackages = PackageUtils.getAllParentPackageNames(interfaceClazz);
        if (matchPackageOrAppName(rpcInfo,parentPackages,excludedAppNames,excludedPackages)){
            return false;
        }
        if (toProviderAppNames.isEmpty() && basePackages.isEmpty()){
            return true;
        }
        return matchPackageOrAppName(rpcInfo,parentPackages,toProviderAppNames,basePackages);
    }

    private boolean matchPackageOrAppName(RpcInfo rpcInfo,@NotNull Set<String> parentPackages,@NotNull Set<String> matchAppNames,@NotNull Set<String> matchPackages){
        if (CollectionUtils.containsAny(parentPackages,matchPackages)){
            return true;
        }
        if (rpcInfo != null){
            return matchAppNames.contains(rpcInfo.appName());
        }
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        initDubboAnnotationExported(applicationContext);
    }

    private void initDubboAnnotationExported (ApplicationContext applicationContext){
        try{
            AnnotationBean annotationBean = applicationContext.getBean(AnnotationBean.class);
            if (annotationBean != null){
                this.annotationBean = annotationBean;
                dubboAnnotationBeanExport = true;
                dubboServiceBeanMatchMethod = ReflectUtils.getMethod(annotationBean,"isMatchPackage",Object.class);
            }
        }catch (Exception e){
            // no do
        }
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

    public void setBasePackages(Set<String> basePackages) {
        this.basePackages = basePackages;
    }

    public void setExcludedAppNames(Set<String> excludedAppNames) {
        this.excludedAppNames = excludedAppNames;
    }

    public void setExcludedPackages(Set<String> excludedPackages) {
        this.excludedPackages = excludedPackages;
    }

    private Class getDubboServiceInterface (Object bean,Class beanClazz){
        if (beanClazz.isAnnotationPresent(Service.class) && checkDubboExport(bean)){
            Service service = (Service) beanClazz.getAnnotation(Service.class);
            if (service.interfaceClass() != null && !void.class.equals(service.interfaceClass())){
                return service.interfaceClass();
            }else if(StringUtils.hasText(service.interfaceName())){
                try{
                    return Class.forName(service.interfaceName());
                }catch (Exception e){
                    throw new RpcProviderExportException("dubbo service interfaceName error",e);
                }
            }else if (beanClazz.getInterfaces().length > 0) {
                return beanClazz.getInterfaces()[0];
            }
        }
        return null;
    }

    private boolean checkDubboExport(Object bean){
        if (annotationBean != null && dubboAnnotationBeanExport && dubboServiceBeanMatchMethod != null){
            try {
                Boolean matched = (Boolean)dubboServiceBeanMatchMethod.invoke(annotationBean,bean);
                return matched ;
            } catch (IllegalAccessException | InvocationTargetException e) {
                return false;
            }
        }
        return false;
    }
}

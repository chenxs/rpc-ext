package cn.hill4j.rpcext.core.rpcext.dubbo;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.config.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import cn.hill4j.rpcext.core.utils.ReflectUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * Description: AnnotationBean驱动的的dubbo rpc 应用级点对点直连扩展
 *
 * @author hillchen
 * @create 2019/8/25 22:31
 */
public class AnnotationBeanDefinitionReset implements BeanPostProcessor,  ApplicationContextAware, PriorityOrdered {
    private static final Logger logger = LoggerFactory.getLogger(Logger.class);

    private ApplicationContext applicationContext;
    private AnnotationBean annotationBean;
    private Map<String, ReferenceBean<?>> referenceConfigs;
    private final String referenceConfigsField = "referenceConfigs";
    private StandardEnvironment env;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.annotationBean = applicationContext.getBean(AnnotationBean.class);
        this.referenceConfigs = (Map<String, ReferenceBean<?>>)ReflectUtils.getBeanFieldVal(annotationBean,referenceConfigsField);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        boolean flag = (Boolean) ReflectUtils.invokeMethod(annotationBean,"isMatchPackage",new Class[]{Object.class},bean);
        if (!flag) {
            return bean;
        }
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.length() > 3 && name.startsWith("set")
                    && method.getParameterTypes().length == 1
                    && Modifier.isPublic(method.getModifiers())
                    && !Modifier.isStatic(method.getModifiers())) {
                try {
                    Reference reference = method.getAnnotation(Reference.class);
                    if (reference != null) {
                        resetRefer(reference, method.getParameterTypes()[0]);
                    }
                } catch (Throwable e) {
                    logger.error("Failed to init remote service reference at method " + name + " in class " + bean.getClass().getName() + ", cause: " + e.getMessage(), e);
                }
            }
        }
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Reference reference = field.getAnnotation(Reference.class);
                if (reference != null) {
                    resetRefer(reference, field.getType());
                }
            } catch (Throwable e) {
                logger.error("Failed to init remote service reference at filed " + field.getName() + " in class " + bean.getClass().getName() + ", cause: " + e.getMessage(), e);
            }
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    /**
     * 初始化referenceConfig并重置url
     * @param reference
     * @param referenceClass
     */
    private void resetRefer(Reference reference, Class<?> referenceClass) {

        if (RpcInfoContext.needResetToDirect(referenceClass,getEnv())){
            String interfaceName;
            if (!"".equals(reference.interfaceName())) {
                interfaceName = reference.interfaceName();
            } else if (!void.class.equals(reference.interfaceClass())) {
                interfaceName = reference.interfaceClass().getName();
            } else if (referenceClass.isInterface()) {
                interfaceName = referenceClass.getName();
            } else {
                throw new IllegalStateException("The @Reference undefined interfaceClass or interfaceName, and the property type " + referenceClass.getName() + " is not a interface.");
            }
            String key = reference.group() + "/" + interfaceName + ":" + reference.version();
            ReferenceBean<?> referenceConfig = referenceConfigs.get(key);
            if (referenceConfig == null) {
                referenceConfig = new ReferenceBean<Object>(reference);
                if (void.class.equals(reference.interfaceClass())
                        && "".equals(reference.interfaceName())
                        && referenceClass.isInterface()) {
                    referenceConfig.setInterface(referenceClass);
                }
                if (applicationContext != null) {
                    referenceConfig.setApplicationContext(applicationContext);
                    if (reference.registry() != null && reference.registry().length > 0) {
                        List<RegistryConfig> registryConfigs = new ArrayList<RegistryConfig>();
                        for (String registryId : reference.registry()) {
                            if (registryId != null && registryId.length() > 0) {
                                registryConfigs.add((RegistryConfig) applicationContext.getBean(registryId, RegistryConfig.class));
                            }
                        }
                        referenceConfig.setRegistries(registryConfigs);
                    }
                    if (reference.consumer() != null && reference.consumer().length() > 0) {
                        referenceConfig.setConsumer((ConsumerConfig) applicationContext.getBean(reference.consumer(), ConsumerConfig.class));
                    }
                    if (reference.monitor() != null && reference.monitor().length() > 0) {
                        referenceConfig.setMonitor((MonitorConfig) applicationContext.getBean(reference.monitor(), MonitorConfig.class));
                    }
                    if (reference.application() != null && reference.application().length() > 0) {
                        referenceConfig.setApplication((ApplicationConfig) applicationContext.getBean(reference.application(), ApplicationConfig.class));
                    }
                    if (reference.module() != null && reference.module().length() > 0) {
                        referenceConfig.setModule((ModuleConfig) applicationContext.getBean(reference.module(), ModuleConfig.class));
                    }
                    if (reference.consumer() != null && reference.consumer().length() > 0) {
                        referenceConfig.setConsumer((ConsumerConfig) applicationContext.getBean(reference.consumer(), ConsumerConfig.class));
                    }
                    try {
                        String url = RpcInfoContext.getDirectUrl(referenceClass,getEnv());
                        if (StringUtils.hasText(url)){
                            referenceConfig.setUrl(url);
                            referenceConfig.setTimeout(1000*60*5);
                        }
                        referenceConfig.afterPropertiesSet();
                    } catch (RuntimeException e) {
                        throw  e;
                    } catch (Exception e) {
                        throw new IllegalStateException(e.getMessage(), e);
                    }
                }
                referenceConfigs.putIfAbsent(key, referenceConfig);
            }
        }
    }

    private StandardEnvironment getEnv(){
        if (env == null){
            env = (StandardEnvironment)applicationContext.getBean(ConfigurableApplicationContext.ENVIRONMENT_BEAN_NAME);
        }
        return env;
    }

}
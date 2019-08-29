package com.hill.rpc.ext.core.rpcext.dubbo;

import com.alibaba.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import com.hill.rpc.ext.core.utils.ReflectUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;

import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * Description: ResetSortPostProcessor
 *
 * @author hillchen
 * @create 2019/8/30 00:52
 */
public class ResetSortPostProcessor implements MergedBeanDefinitionPostProcessor,ApplicationContextAware, PriorityOrdered {
    private volatile boolean hasResetSort = false;
    private ApplicationContext applicationContext ;

    @Override
    public synchronized void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        if (!hasResetSort){
            Object beanFactory = ReflectUtils.getBeanFieldVal(applicationContext,"beanFactory");
            if (beanFactory != null){
                List<BeanPostProcessor> beanPostProcessors = (List<BeanPostProcessor>)ReflectUtils.getBeanFieldVal(beanFactory,"beanPostProcessors");

                // 如果dubbo自带的postProcess在列表的前面，则两个对象就在列表中互换位置
                ReferenceAnnotationBeanPostProcessor referenceAnnotationBeanPostProcessor = applicationContext.getBean(ReferenceAnnotationBeanPostProcessor.class);
                ResetRpcDefinitionBeanPostProcessor resetRpcDefinitionBeanPostProcessor = applicationContext.getBean(ResetRpcDefinitionBeanPostProcessor.class);

                int referenceIndex = beanPostProcessors.indexOf(referenceAnnotationBeanPostProcessor);
                int resetIndex = beanPostProcessors.indexOf(resetRpcDefinitionBeanPostProcessor);

                if (referenceIndex >= 0 && resetIndex >= 0 && resetIndex < referenceIndex){
                    beanPostProcessors.set(referenceIndex,resetRpcDefinitionBeanPostProcessor);
                    beanPostProcessors.set(resetIndex,referenceAnnotationBeanPostProcessor);
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE - 1;
    }
}
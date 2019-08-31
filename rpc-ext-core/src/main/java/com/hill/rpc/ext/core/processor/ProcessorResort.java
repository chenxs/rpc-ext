package com.hill.rpc.ext.core.processor;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;

/**
 * 〈一句话功能简述〉<br>
 * Description: beanPostProcessor顺序重组配置bean
 *
 * @author hillchen
 * @create 2019/8/31 13:06
 */
public interface ProcessorResort {

    /**
     * 获取前置beanProcessor
     * @param applicationContext
     * @return
     */
    BeanPostProcessor getBeforeBeanProcessor(ApplicationContext applicationContext) ;

    /**
     * 获取后置beanProcessor
     * @param applicationContext
     * @return
     */
    BeanPostProcessor getAfterBeanProcessor(ApplicationContext applicationContext);

    /**
     * 是否固定重排后置bean
     * @return
     */
    boolean afterFixed();

}
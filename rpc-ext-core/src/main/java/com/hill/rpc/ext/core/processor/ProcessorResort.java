package com.hill.rpc.ext.core.processor;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;

/**
 * 〈一句话功能简述〉<br>
 * Description: beanPostProcessor顺序重组bean
 *
 * @author hillchen
 * @create 2019/8/31 13:06
 */
public interface ProcessorResort {

    BeanPostProcessor getBeforeBeanProcessor(ApplicationContext applicationContext) ;

    BeanPostProcessor getAfterBeanProcessor(ApplicationContext applicationContext);

    /**
     * 是否固定重排后置bean
     * @return
     */
    boolean afterFixed();

}
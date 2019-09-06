package cn.hill4j.rpcext.core.processor;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;

/**
 *  2019/8/31 13:06 <br>
 * Description: beanPostProcessor顺序重组配置bean
 *
 * @author hillchen
 */
public interface ProcessorResort {

    /**
     * 获取前置beanProcessor
     * @param applicationContext Spring 容器上下文
     * @return 从容器中获取需要前置的BeanProcessor
     */
    BeanPostProcessor getBeforeBeanProcessor(ApplicationContext applicationContext) ;

    /**
     * 获取后置beanProcessor
     * @param applicationContext Spring 容器上下文
     * @return 从容器中获取需要后置置的BeanProcessor
     */
    BeanPostProcessor getAfterBeanProcessor(ApplicationContext applicationContext);

    /**
     * 是否固定重排后置bean
     * @return 如果是需要将原列表中的后序的BeanProcessor保持跟原列表其他的BeanProcessor顺序不变则返回true,
     * 如果是需要将原列表中的前序的BeanProcessor保持跟原列表其他的BeanProcessor顺序不变则返回false
     */
    boolean afterFixed();

}

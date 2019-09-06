package cn.hill4j.rpcext.core.processor;

import cn.hill4j.rpcext.core.utils.ListResortUtils;
import cn.hill4j.rpcext.core.utils.ReflectUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Map;

/**
 * 2019/8/30 00:52 <br>
 * Description: 将spring容器中的BeanPostProcessor列表在执行前做一次重排,
 * 以解决在各个第三方库中将BeanPostProcessor的执行顺序放到了最后面，或没有设置排序值,
 * 而自己扩展的功能有必须保证与第三方定义的BeanPostProcessor保持一定执行顺序
 *
 * @author hillchen
 */
public class ResetSortPostProcessor implements MergedBeanDefinitionPostProcessor,ApplicationContextAware, PriorityOrdered {
    private volatile boolean hasResetSort = false;
    private ApplicationContext applicationContext ;

    @Override
    public synchronized void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        if (!hasResetSort){
            hasResetSort = true;
            Map<String, ProcessorResort> ProcessorResters =  applicationContext.getBeansOfType(ProcessorResort.class);
            if (CollectionUtils.isEmpty(ProcessorResters)){
                return;
            }else {
                Object beanFactory = ReflectUtils.getBeanFieldValNoError(applicationContext,"beanFactory");
                if (beanFactory != null){
                    List<BeanPostProcessor> beanPostProcessors = (List<BeanPostProcessor>)ReflectUtils.getBeanFieldValNoError(beanFactory,"beanPostProcessors");
                    ProcessorResters.values()
                            .stream()
                            .forEach(processorRester -> resetPostProcessor(processorRester,beanPostProcessors));
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
        return HIGHEST_PRECEDENCE;
    }

    private void resetPostProcessor(ProcessorResort processorRester, List<BeanPostProcessor> beanPostProcessors){
        if (processorRester != null ){
            BeanPostProcessor beforeBeanProcessor = processorRester.getBeforeBeanProcessor(applicationContext);
            BeanPostProcessor afterBeanProcessor = processorRester.getAfterBeanProcessor(applicationContext);
            if (beforeBeanProcessor == null || afterBeanProcessor == null){
                return;
            }
            ListResortUtils.resortList(beanPostProcessors,beforeBeanProcessor,afterBeanProcessor,processorRester.afterFixed());
        }
    }
}

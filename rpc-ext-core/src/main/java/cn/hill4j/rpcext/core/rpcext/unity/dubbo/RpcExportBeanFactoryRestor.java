package cn.hill4j.rpcext.core.rpcext.unity.dubbo;

import cn.hill4j.rpcext.core.processor.BeanFactoryProcessorResort;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.ApplicationContext;

/**
 * 2019/9/20 17:11<br>
 * Description: dubbo rpc 服务暴露顺序重置类
 *
 * @author hillchen
 */
public class RpcExportBeanFactoryRestor implements BeanFactoryProcessorResort {

    @Override
    public BeanFactoryPostProcessor getBeforeFactoryBeanProcessor(ApplicationContext applicationContext) {
        try{
            return applicationContext.getBean(AnnotationBean.class);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public BeanFactoryPostProcessor getAfterFactoryBeanProcessor(ApplicationContext applicationContext) {
        try{
            return applicationContext.getBean("rpcProviderLoadPostProcessor",RpcProviderLoadPostProcessor.class);
        }catch (Exception e){
            return null;
        }

    }

    @Override
    public boolean afterFixed() {
        return false;
    }
}

package cn.hill4j.rpcext.core.processor.configuration;

import cn.hill4j.rpcext.core.processor.ProcessorResort;
import cn.hill4j.rpcext.core.processor.ResetSortPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 2019/8/26 16:40 <br>
 * Description: RPC扩展功能自动装置bean
 *
 * @author hillchen
 */
@Configuration
public class ProcessorResortConfiguration {
    @Bean
    @ConditionalOnBean(ProcessorResort.class)
    public ResetSortPostProcessor ResetSortPostProcessor(){
        return new ResetSortPostProcessor();
    }
}

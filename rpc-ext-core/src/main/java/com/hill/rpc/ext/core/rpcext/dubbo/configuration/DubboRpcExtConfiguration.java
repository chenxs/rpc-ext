package com.hill.rpc.ext.core.rpcext.dubbo.configuration;

import com.alibaba.dubbo.config.spring.schema.DubboNamespaceHandler;
import com.hill.rpc.ext.core.rpcext.dubbo.ResetRpcDefinition;
import com.hill.rpc.ext.core.rpcext.dubbo.ResetRpcDefinitionBeanPostProcessor;
import com.hill.rpc.ext.core.rpcext.dubbo.ResetSortPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 〈一句话功能简述〉<br>
 * Description: RPC扩展功能自动装置bean
 *
 * @author hillchen
 * @create 2019/8/26 16:40
 */
@Configuration
@ConditionalOnClass({DubboNamespaceHandler.class})
public class DubboRpcExtConfiguration {
    @Bean
    public  ResetRpcDefinition resetRpcDefinition(){
        return new ResetRpcDefinition();
    }

    @Bean
    public ResetRpcDefinitionBeanPostProcessor resetRpcDefinitionBeanPostProcessor(){
        return new ResetRpcDefinitionBeanPostProcessor();
    }

    @Bean
    public ResetSortPostProcessor ResetSortPostProcessor(){
        return new ResetSortPostProcessor();
    }
}
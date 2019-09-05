package com.github.chenxs.rpc.ext.demo.dubbo.client.test;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

/**
 * 〈一句话功能简述〉<br>
 * Description: SerApplication
 *
 * @author hillchen
 * @create 2019/8/28 19:23
 */
@SpringBootApplication
@ImportResource({ "classpath:/dubboConfig.xml"})
public class SpringApplicationTest {
   @Bean
    public TestBeanFactoryPostProcessor testBeanFactoryPostProcessor(){
        return new TestBeanFactoryPostProcessor();
    }

    @Bean
    public TestBeanPostProcessor testBeanPostProcessor(){
        return new TestBeanPostProcessor();
    }

   @Bean
    public TestInstantiationAwareBeanPostProcessor testInstantiationAwareBeanPostProcessor(){
        return new TestInstantiationAwareBeanPostProcessor();
    }

    @Bean
    public TestMergedBeanDefinitionPostProcessor testMergedBeanDefinitionPostProcessor(){
        return new TestMergedBeanDefinitionPostProcessor();
    }

    /*@Bean
    public TestSmartInstantiationAwareBeanPostProcessor testSmartInstantiationAwareBeanPostProcessor(){
        return new TestSmartInstantiationAwareBeanPostProcessor();
    }*/

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringApplicationTest.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}

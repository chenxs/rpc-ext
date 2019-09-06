package cn.hill4j.rpcext.demo.dubbo.client.test;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

/**
 * 〈一句话功能简述〉<br>
 * Description: SerApplication
 *
 * @author hillchen
 * @create 2019/8/28 19:23
 */
@SpringBootApplication
@EnableDubbo
public class SpringApplicationTest {
    private Logger logger = LoggerFactory.getLogger(SpringApplicationTest.class);

    @Bean
    public TestBeanDefinitionRegistryPostProcessor testBeanDefinitionRegistryPostProcessor(){
        return new TestBeanDefinitionRegistryPostProcessor();
    }
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

    @Bean
    public TestBean testBean(){
        logger.error("SpringApplicationTest.testBean");
       return new TestBean();
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

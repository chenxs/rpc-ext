package cn.hill4j.rpcext.demo.dubbo.client;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
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
public class AnnotationBeanClientApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AnnotationBeanClientApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
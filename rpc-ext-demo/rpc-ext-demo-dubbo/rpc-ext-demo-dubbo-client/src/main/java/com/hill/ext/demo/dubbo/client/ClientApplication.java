package com.hill.ext.demo.dubbo.client;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 〈一句话功能简述〉<br>
 * Description: SerApplication
 *
 * @author hillchen
 * @create 2019/8/28 19:23
 */
@SpringBootApplication
@EnableDubbo
public class ClientApplication{
    public static void main(String[] args) {
        new SpringApplicationBuilder(ClientApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
package com.hill.ext.demo.dubbo.service;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
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
public class SerApplication implements ApplicationRunner {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SerApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        while (true){
            Thread.sleep(100000);
        }
    }
}
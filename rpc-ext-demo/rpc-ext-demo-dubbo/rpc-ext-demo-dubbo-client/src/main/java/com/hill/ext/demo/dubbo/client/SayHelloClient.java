package com.hill.ext.demo.dubbo.client;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hill.ext.demo.dubbo.api.SayHello;
import org.springframework.stereotype.Component;

/**
 * 〈一句话功能简述〉<br>
 * Description: SayHelloClient
 *
 * @author hillchen
 * @create 2019/8/28 19:34
 */
@Component
public class SayHelloClient {
    @Reference
    private SayHello sayHello;

    public String sayHello(String name){
        return sayHello.sayHello() + ", " + name;
    }
}
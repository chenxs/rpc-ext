package com.hill.ext.demo.dubbo.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hill.ext.demo.dubbo.api.SayHello;
import org.springframework.stereotype.Component;

/**
 * 〈一句话功能简述〉<br>
 * Description: SayHelloImpl
 *
 * @author hillchen
 * @create 2019/8/28 19:33
 */
@Service
@Component
public class SayHelloImpl implements SayHello {

    @Override
    public String sayHello() {
        return  System.currentTimeMillis() + ": hello";
    }
}
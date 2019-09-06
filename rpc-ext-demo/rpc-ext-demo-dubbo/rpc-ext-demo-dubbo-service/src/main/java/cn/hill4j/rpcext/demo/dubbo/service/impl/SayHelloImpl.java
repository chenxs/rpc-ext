package cn.hill4j.rpcext.demo.dubbo.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import cn.hill4j.rpcext.demo.dubbo.api.SayHello;
import org.springframework.stereotype.Component;

/**
 * 2019/8/28 19:33 <br>
 * Description: SayHelloImpl
 *
 * @author hillchen
 */
@Service
@Component
public class SayHelloImpl implements SayHello {

    @Override
    public String sayHello() {
        return  System.currentTimeMillis() + ": hello";
    }
}

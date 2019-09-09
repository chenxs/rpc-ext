package cn.hill4j.rpcext.demo.dubbo.client.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 〈一句话功能简述〉<br>
 * Description: TestBean
 *
 * @author hillchen
 * @create 2019/9/5 18:33
 */
@Component
public class TestBean2 {
    @Autowired
    private TestParam testParam;
    public String say(){
        return testParam.say();
    }
}

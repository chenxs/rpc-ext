package com.hill.ext.demo.dubbo.client.test;

import com.hill.ext.demo.dubbo.client.ClientApplication;
import com.hill.ext.demo.dubbo.client.SayHelloClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * 〈一句话功能简述〉<br>
 * Description: SayHelloClientTest
 *
 * @author hillchen
 * @create 2019/8/29 10:43
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ClientApplication.class)
public class SayHelloClientTest {
    @Resource
    private SayHelloClient sayHelloClient;

    @Test
    public void sayHelloTest(){
        String say = sayHelloClient.sayHello("hill chen");
        Assert.hasLength(say,"need say something!");
        System.out.println(say);

    }
}
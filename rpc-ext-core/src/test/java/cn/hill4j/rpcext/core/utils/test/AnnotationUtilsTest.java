package cn.hill4j.rpcext.core.utils.test;

import cn.hill4j.rpcext.core.rpcext.dubbo.annotation.RpcApi;
import cn.hill4j.rpcext.core.utils.AnnotationUtils;
import cn.hill4j.rpcext.core.utils.test.annotation.AnnoTest;
import cn.hill4j.rpcext.core.utils.test.annotation.PkgNoAnnoTest;
import cn.hill4j.rpcext.core.utils.test.annotation.test1.test2.PkgNoAnnoTest2;
import cn.hill4j.rpcext.core.utils.test.annotation.test1.test2.PkgAnnoTest4;
import cn.hill4j.rpcext.core.utils.test.annotation.test1.test3.PkgAnnoTest5;
import cn.hill4j.rpcext.core.utils.test.annotation.test1.test3.PkgNoAnnoTest1;
import com.alibaba.dubbo.config.annotation.Service;
import org.junit.Test;
import org.springframework.util.Assert;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * Description: AnnotationUtilsTest
 *
 * @author hillchen
 * @create 2019/8/25 12:02
 */
public class AnnotationUtilsTest {
    @Test
    public void superTest(){
        boolean flag = new SubClass().isInitialized();
        System.out.println(flag);
    }

    @Test
    public void recursionGetTest(){
        AnnoTest annoTest1 = AnnotationUtils.recursionGet(PkgNoAnnoTest1.class,AnnoTest.class);
        Assert.isTrue(annoTest1.value() == 1,"");

        AnnoTest annoTest2 = AnnotationUtils.recursionGet(PkgNoAnnoTest2.class,AnnoTest.class);
        Assert.isTrue(annoTest2.value() == 2,"");


        AnnoTest annoTest4 = AnnotationUtils.recursionGet(PkgAnnoTest4.class,AnnoTest.class);
        Assert.isTrue(annoTest4.value() == 4,"");


        AnnoTest annoTest5 = AnnotationUtils.recursionGet(PkgAnnoTest5.class,AnnoTest.class);
        Assert.isTrue(annoTest5.value() == 5,"");

        AnnoTest annoTestNull = AnnotationUtils.recursionGet(PkgNoAnnoTest.class,AnnoTest.class);
        Assert.isNull(annoTestNull,"");
    }

    @Test
    public void createRpcApi () throws Exception {
        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("version","1.1.0");
        paramsMap.put("delay",12);
        RpcApi rpcApi = AnnotationUtils.createAnnotationObj(RpcApi.class,paramsMap);
        Assert.isTrue("1.1.0".equals(rpcApi.version()),"");

        Service service = AnnotationUtils.transformToOther(rpcApi,Service.class);
        Assert.isTrue("1.1.0".equals(service.version()),"");


    }

    @Test
    public void bigDecimalTest(){
        BigDecimal bigDecimal1= new BigDecimal("12745.12345").setScale(2, RoundingMode.DOWN);
        BigDecimal bigDecimal2= new BigDecimal("12745.125").setScale(2, RoundingMode.DOWN);
        BigDecimal bigDecimal3= new BigDecimal("12745.124").setScale(2, RoundingMode.DOWN);

        BigDecimal bigDecimal = bigDecimal1.add(bigDecimal2).add(bigDecimal3);

        System.out.println(bigDecimal.doubleValue());

    }

}

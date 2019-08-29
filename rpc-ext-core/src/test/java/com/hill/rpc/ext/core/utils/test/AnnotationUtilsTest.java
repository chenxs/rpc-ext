package com.hill.rpc.ext.core.utils.test;

import com.hill.rpc.ext.core.utils.AnnotationUtils;
import com.hill.rpc.ext.core.utils.test.annotation.AnnoTest;
import com.hill.rpc.ext.core.utils.test.annotation.PkgNoAnnoTest;
import com.hill.rpc.ext.core.utils.test.annotation.test1.test2.PkgNoAnnoTest2;
import com.hill.rpc.ext.core.utils.test.annotation.test1.test2.PkgAnnoTest4 ;
import com.hill.rpc.ext.core.utils.test.annotation.test1.test3.PkgAnnoTest5 ;
import com.hill.rpc.ext.core.utils.test.annotation.test1.test3.PkgNoAnnoTest1;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * 〈一句话功能简述〉<br>
 * Description: AnnotationUtilsTest
 *
 * @author hillchen
 * @create 2019/8/25 12:02
 */
public class AnnotationUtilsTest {

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
}
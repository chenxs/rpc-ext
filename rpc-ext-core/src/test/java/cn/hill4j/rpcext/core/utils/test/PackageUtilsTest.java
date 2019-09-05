package cn.hill4j.rpcext.core.utils.test;

import cn.hill4j.rpcext.core.utils.test.annotation.test1.test3.PkgNoAnnoTest1;
import org.junit.Test;
import cn.hill4j.rpcext.core.utils.PackageUtils;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * Description: java类包工具类测试
 *
 * @author hillchen
 * @create 2019/8/25 11:20
 */
public class PackageUtilsTest {
    @Test
    public void getAllParentPackageTest(){
        Package  currentPackage = getClass().getPackage();

        List<Package> parentPackages = PackageUtils.allParent(currentPackage);

        Assert.notEmpty(parentPackages,"父包不为空");

        List<Package> parentPackages1 = PackageUtils.allParent(PkgNoAnnoTest1.class.getPackage());

        Assert.notEmpty(parentPackages1,"父包不为空");
    }

    @Test
    public void getPackageTest(){
        Package packageInfo = PackageUtils.getPackage("com.test.hill");
        System.out.println(packageInfo);
    }
}
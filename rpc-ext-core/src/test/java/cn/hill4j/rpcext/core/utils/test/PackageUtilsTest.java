package cn.hill4j.rpcext.core.utils.test;

import cn.hill4j.rpcext.core.rpcext.dubbo.annotation.RpcInfo;
import cn.hill4j.rpcext.core.utils.test.annotation.AnnoTest;
import cn.hill4j.rpcext.core.utils.test.annotation.test1.test3.PkgNoAnnoTest1;
import org.junit.Test;
import cn.hill4j.rpcext.core.utils.PackageUtils;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Test
    public void getPackageInfoTest()  {
        Set<Package> packages = PackageUtils.getAllPackageInfo(this.getClass().getPackage().getName(),pck -> pck.isAnnotationPresent(AnnoTest.class),new StandardEnvironment());
        Assert.notEmpty(packages);
    }

    @Test
    public void reducePackagesTest()  {
        Set<String> pkgs = new HashSet<>();
        pkgs.add("cn.hill4j.test1");
        pkgs.add("cn.hill4j.test2");
        pkgs.add("cn.hill4j.test1.test3");
        pkgs.add("cn.hill4j.test3.test3");
        pkgs.add("cn.hill4j.test4.test1");
        Set<String> packages = PackageUtils.reducePackages(pkgs);
        Assert.isTrue(packages.size() == 4);
    }
}

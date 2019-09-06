package cn.hill4j.rpcext.core.utils;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 2019/8/25 11:05 <br>
 * Description: java类包工具类
 *
 * @author hillchen
 */
public class PackageUtils {
    private PackageUtils(){}

    /**
     * 获取指定包的所有父包，关系越近，在列表中越前
     * @param pkg 包对象
     * @return 指定包的所有父包
     */
    public static List<Package> allParent(Package pkg){
        List<Package> packages = new ArrayList<>();
        allParent(pkg.getName(),packages);
        return packages;
    }

    private static void allParent(String pkgName,List<Package> packages){
        int index = pkgName.lastIndexOf(".");
        if (index > 0){
            String parentPckName = pkgName.substring(0,index);
            Package parentPck = getPackage(parentPckName);
            if (parentPck != null){
                allParent(parentPckName,packages);
                packages.add(parentPck);
            }
        }
    }

    /**
     * 获取父包，当父包空间以及父包的所有祖宗包下没有定义java类并且没有定义package-info时返回空
     * @param pkg 包对象
     * @return 父包，当父包空间以及父包的所有祖宗包下没有定义java类并且没有定义package-info时返回空
     */
    public static Package getParent(Package pkg){
        if (Objects.isNull(pkg)){
            return null;
        }
        String pkgName = pkg.getName();
        String parentPckName = getParentPackageName(pkgName);
        if (StringUtils.hasText(parentPckName)){
            return getPackage(parentPckName);
        }
        return null;
    }

    /**
     * 根据当前包名获取父包名
     * @param pkgName 包名
     * @return 获取父包的全路径名称
     */
    public static String getParentPackageName(String pkgName){
        if (!StringUtils.hasText(pkgName)){
            return null;
        }
        int index = pkgName.lastIndexOf(".");
        if (index > 0){
            return pkgName.substring(0,index);
        }
        return null;
    }

    /**
     * 根据报名获取包对象：如果该包下没有定义java类并且没有定义package-info，则返回的包对象为空
     * @param pkgName 包名
     * @return 包对象
     */
    public static Package getPackage(String pkgName){
        try {
            // 如果包下没有java类被加载过，就获取不到这个包的package-info信息,所以必须先对包信息进行初始化
            Class.forName(pkgName + ".package-info");
        } catch (ClassNotFoundException e) {
            //Ignore
        }
        return Package.getPackage(pkgName);
    }

}

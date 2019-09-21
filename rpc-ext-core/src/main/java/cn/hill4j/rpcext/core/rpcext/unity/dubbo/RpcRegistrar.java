package cn.hill4j.rpcext.core.rpcext.unity.dubbo;

import cn.hill4j.rpcext.core.rpcext.unity.dubbo.annotation.EnableRpcProvider;
import cn.hill4j.rpcext.core.utils.PackageUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 2019/9/11 15:54<br>
 * Description: 服务暴露bean初始化处理器
 *
 * @author hillchen
 */
public class RpcRegistrar {
    protected Set<String> getAttributesToSet(AnnotationAttributes attributes,String attrName){
        if (attributes == null){
            return new HashSet<>();
        }
        String[] attrValues = attributes.getStringArray(attrName);
        if (attrValues == null){
            return new HashSet<>();
        }else {
            return new HashSet<>(Arrays.asList(attrValues));
        }
    }

    protected boolean isLoader(String className){
        ClassLoader classLoader = RpcRegistrar.class.getClassLoader();
        try {
            classLoader.loadClass(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}

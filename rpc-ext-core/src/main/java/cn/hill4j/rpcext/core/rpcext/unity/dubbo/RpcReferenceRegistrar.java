package cn.hill4j.rpcext.core.rpcext.unity.dubbo;

import cn.hill4j.rpcext.core.rpcext.ClassPathScanningPackageInfoProvider;
import cn.hill4j.rpcext.core.rpcext.dubbo.annotation.RpcApi;
import cn.hill4j.rpcext.core.rpcext.dubbo.annotation.RpcInfo;
import cn.hill4j.rpcext.core.rpcext.unity.dubbo.annotation.EnableRpcProvider;
import cn.hill4j.rpcext.core.utils.PackageUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import cn.hill4j.rpcext.core.rpcext.unity.dubbo.annotation.EnableRpcReferences;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import sun.reflect.annotation.AnnotationType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 2019/9/11 15:54<br>
 * Description: 服务暴露bean初始化处理器
 *
 * @author hillchen
 */
public class RpcReferenceRegistrar implements ImportBeanDefinitionRegistrar ,
        ResourceLoaderAware, EnvironmentAware {
    private ResourceLoader resourceLoader;
    private Set<String> referenceAttrNames;
    private Environment environment;
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        registerRpcReferences(metadata, registry);
    }

    public void registerRpcReferences(AnnotationMetadata metadata,
                                     BeanDefinitionRegistry registry){
        // 获取类扫描器
        ClassPathScanningPackageInfoProvider scanner = new ClassPathScanningPackageInfoProvider(environment);
        scanner.setResourceLoader(this.resourceLoader);
        Set<String> scanBasePackages = getBasePackages(metadata,scanner);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(
                RpcApi.class);
        scanner.addIncludeFilter(annotationTypeFilter);

        for (String basePackage : scanBasePackages) {
            Set<BeanDefinition> candidateComponents = scanner
                    .findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    // verify annotated class is an interface
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(annotationMetadata.isInterface(),
                            "@RpcApi can only be specified on an interface");

                    Map<String, Object> attributes = annotationMetadata
                            .getAnnotationAttributes(
                                    RpcApi.class.getCanonicalName());
                    registerFeignClient(registry,annotationMetadata,attributes);
                }
            }
        }
    }

    private void registerFeignClient(BeanDefinitionRegistry registry,
                                     AnnotationMetadata annotationMetadata, Map<String, Object> attributes){
        final Set<String> referenceAttrNames = getReferenceAttrNames();
        String className = annotationMetadata.getClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder
                .genericBeanDefinition(ReferenceBean.class);
        attributes.forEach((k,v) -> {
            if (referenceAttrNames.contains(k)){
                definition.addPropertyValue(k,v);
            }
        });
        definition.addPropertyValue("interface", className);
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        String alias = "rpcApi:" + className ;
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className,
                new String[] { alias });
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private Set<String> getBasePackages(AnnotationMetadata metadata, ClassPathScanningPackageInfoProvider classPathScanningPackinfoProvider){

        AnnotationAttributes providerAttrs = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(EnableRpcProvider.class.getName()));
        AnnotationAttributes referenceAttrs = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(EnableRpcReferences.class.getName()));

        String[] providerAppNames = null;
        String[] referenceAppNames = null;
        String[] orgBasePackages = null;
        if (providerAttrs != null){
            providerAppNames = (String[] ) providerAttrs.get("value");
        }
        if (referenceAttrs != null){
            referenceAppNames = (String[] ) referenceAttrs.get("referenceAppNames");
            orgBasePackages = (String[] ) referenceAttrs.get("orgBasePackages");
        }

        List<String> rpcInfoScanPackageNames ;

        if (orgBasePackages != null){
            rpcInfoScanPackageNames = Arrays.asList(orgBasePackages);
        }else {
            rpcInfoScanPackageNames = Collections.emptyList();
        }
        Set<Package> rpcInfoPackages = classPathScanningPackinfoProvider.scanPackages(rpcInfoScanPackageNames,pkg -> pkg.isAnnotationPresent(RpcInfo.class));
        if (CollectionUtils.isEmpty(rpcInfoPackages)){
            return PackageUtils.reducePackages(rpcInfoScanPackageNames);
        }else{
            Map<String,Package> rpcInfoMap = rpcInfoPackages
                    .stream()
                    .collect(Collectors.toMap(
                            pkg -> pkg.getAnnotation(RpcInfo.class).appName(),
                            pkg -> pkg
                    ));

            Set<String> rpcApiBasePackages = new HashSet<>();
            final Set<String> needFilterAppName = new HashSet<>();
            if (providerAppNames != null){
                Stream.of(providerAppNames).forEach(appName -> needFilterAppName.add(appName));
            }

            if (referenceAppNames != null){
                rpcApiBasePackages = Stream.of(referenceAppNames).filter(appName -> !needFilterAppName.contains(appName))
                        .filter(appName -> rpcInfoMap.containsKey(appName))
                        .map(appName -> rpcInfoMap.get(appName).getName())
                        .collect(Collectors.toSet());
            }
            return PackageUtils.reducePackages(rpcApiBasePackages);
        }
    }

    private Set<String> getReferenceAttrNames(){
        if (referenceAttrNames == null){
            referenceAttrNames = AnnotationType.getInstance(Reference.class).memberTypes().keySet();
            referenceAttrNames.remove("interfaceClass");
            referenceAttrNames.remove("interfaceName");
        }
        return referenceAttrNames;
    }

}

package cn.hill4j.rpcext.core.rpcext.unity.dubbo;

import cn.hill4j.rpcext.core.rpcext.ClassPathScanningPackageInfoProvider;
import cn.hill4j.rpcext.core.rpcext.direct.dubbo.RpcInfoContext;
import cn.hill4j.rpcext.core.rpcext.dubbo.annotation.RpcApi;
import cn.hill4j.rpcext.core.rpcext.dubbo.annotation.RpcInfo;
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
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import sun.reflect.annotation.AnnotationType;

import java.io.IOException;
import java.util.*;

/**
 * 2019/9/11 15:54<br>
 * Description: 服务暴露bean初始化处理器
 *
 * @author hillchen
 */
public class RpcReferenceRegistrar extends RpcRegistrar implements ImportBeanDefinitionRegistrar ,
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
        AnnotationAttributes referenceAttrs = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(EnableRpcReferences.class.getName()));
        if (referenceAttrs == null){
            return;
        }
        Set<String> referenceAppNames = getAttributesToSet(referenceAttrs,"referenceAppNames");
        Set<String> excludedAppNames = getAttributesToSet(referenceAttrs,"excludedAppNames");
        Set<String> excludedPackages = PackageUtils.reducePackages(getAttributesToSet(referenceAttrs,"excludedPackages"));

        // 获取类扫描器
        ClassPathScanningPackageInfoProvider scanner = new ClassPathScanningPackageInfoProvider(environment);
        scanner.setResourceLoader(this.resourceLoader);

        ReferenceBeanFilter referenceBeanFilter = new ReferenceBeanFilter(referenceAppNames, excludedAppNames,excludedPackages);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(
                RpcApi.class);

        scanner.addIncludeFilter(new AllTypeFilter(Arrays.asList(annotationTypeFilter,referenceBeanFilter)));

        Set<String> scanBasePackages = getBasePackages(metadata,referenceAttrs);
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
                    registerReferenceBean(registry,annotationMetadata,attributes);
                }
            }
        }
    }

    private void registerReferenceBean(BeanDefinitionRegistry registry,
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

    private Set<String> getBasePackages(AnnotationMetadata metadata,AnnotationAttributes referenceAttrs){
        Set<String> scanPackageNames = getAttributesToSet(referenceAttrs,"basePackages");

        if (CollectionUtils.isEmpty(scanPackageNames)){
           return new HashSet<>(Arrays.asList(ClassUtils.getPackageName(metadata.getClassName())));
        }else {
            return PackageUtils.reducePackages(scanPackageNames);
        }
    }

    /**
     *
     * @return 获取构造referenceBean的BeanDefinition所需要的属性字段名
     */
    private Set<String> getReferenceAttrNames(){
        if (referenceAttrNames == null){
            referenceAttrNames = AnnotationType.getInstance(Reference.class).memberTypes().keySet();
            referenceAttrNames.remove("interfaceClass");
            referenceAttrNames.remove("interfaceName");
        }
        return referenceAttrNames;
    }

    private class ReferenceBeanFilter implements TypeFilter {
        private Set<String> includeAppNames;
        private Set<String> excludeAppNames;
        private  Set<String> excludedPackages;

        public ReferenceBeanFilter(Set<String> includeAppNames, Set<String> excludeAppNames,Set<String> excludedPackages) {
            this.includeAppNames = includeAppNames;
            this.excludeAppNames = excludeAppNames;
            this.excludedPackages = excludedPackages;
        }

        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
            ClassMetadata classMetadata = metadataReader.getClassMetadata();
            String className = classMetadata.getClassName();
            Set<String> parentPackageNames = PackageUtils.getAllParentPackageNames(className);
            if (CollectionUtils.containsAny(parentPackageNames,excludedPackages)){
                return false;
            }
            RpcInfo rpcInfo = RpcInfoContext.getAppRpcInfo(className);
            if (rpcInfo != null){
                String currentAppName = rpcInfo.appName();
                if (excludeAppNames.contains(currentAppName)){
                    return false;
                }
                if (CollectionUtils.isEmpty(includeAppNames)){
                    return true;
                }
                return includeAppNames.contains(currentAppName);
            }else {
                return CollectionUtils.isEmpty(includeAppNames);
            }
        }
    }

    private static class AllTypeFilter implements TypeFilter {

        private final List<TypeFilter> delegates;

        public AllTypeFilter(List<TypeFilter> delegates) {
            Assert.notNull(delegates, "This argument is required, it must not be null");
            this.delegates = delegates;
        }

        @Override
        public boolean match(MetadataReader metadataReader,
                             MetadataReaderFactory metadataReaderFactory) throws IOException {

            for (TypeFilter filter : this.delegates) {
                if (!filter.match(metadataReader, metadataReaderFactory)) {
                    return false;
                }
            }
            return true;
        }
    }
}

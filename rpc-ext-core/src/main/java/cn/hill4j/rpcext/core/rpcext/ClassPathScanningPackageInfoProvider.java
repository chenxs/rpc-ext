package cn.hill4j.rpcext.core.rpcext;

import cn.hill4j.rpcext.core.rpcext.exception.PackageInfoScanException;
import cn.hill4j.rpcext.core.utils.PackageUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 2019/9/18 09:43<br>
 * Description: classpath 下page-info查找类
 *
 * @author hillchen
 */
public class ClassPathScanningPackageInfoProvider extends ClassPathScanningCandidateComponentProvider {
    private final String PACKAGE_INFO_RESOURCE_PATTERN = "**/package-info.class";
    public ClassPathScanningPackageInfoProvider() {
    }
    public ClassPathScanningPackageInfoProvider(boolean useDefaultFilters) {
        super(useDefaultFilters);
    }

    public ClassPathScanningPackageInfoProvider(boolean useDefaultFilters, Environment environment) {
        super(useDefaultFilters, environment);
    }

    public ClassPathScanningPackageInfoProvider(Environment environment) {
        this(false, environment);
    }

    public Set<Package> scanPackages(String basePackage){
        return scanPackages(basePackage, pkg -> true);
    }

    public Set<Package> scanPackages(final String basePackage, final Predicate<Package> filter) {
        Predicate<Package> packagePredicate = filter == null ? pkg -> true : filter;
        Environment environment = getEnvironment();
        String basePath = ClassUtils.convertClassNameToResourcePath(environment.resolveRequiredPlaceholders(basePackage));
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                basePath + '/' + PACKAGE_INFO_RESOURCE_PATTERN;
        ResourcePatternResolver resourcePatternResolver = (ResourcePatternResolver) getResourceLoader();
        try{
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            Set<Package> packageSet = new HashSet<>();
            for (Resource resource : resources){
                if (resource.isReadable()){
                    MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                    ClassMetadata metadata =  metadataReader.getClassMetadata();
                    Class clazz = Class.forName(metadata.getClassName());
                    Package currPackage = clazz.getPackage();
                    if (packagePredicate.test(currPackage)){
                        packageSet.add(currPackage);
                    }
                }
            }
            return packageSet;
        }catch (Exception e){
            throw new PackageInfoScanException(" classpath scanning package-info error", e);
        }
    }

    public Set<Package> scanPackages(final Collection<String> basePackages){
        return scanPackages(basePackages, pkg -> true);
    }

    public Set<Package> scanPackages(final Collection<String> basePackages, final Predicate<Package> filter) {
        if (CollectionUtils.isEmpty(basePackages)){
            return scanPackages("",filter);
        }else {
            Set<String> scanPackages = PackageUtils.reducePackages(basePackages);
            Set<Package> result = new HashSet<>();
            scanPackages.forEach(packageName -> {
                result.addAll(scanPackages(packageName,filter));
            });
            return result;
        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        boolean isCandidate = false;
        if (beanDefinition.getMetadata().isIndependent()) {
            if (!beanDefinition.getMetadata().isAnnotation()) {
                isCandidate = true;
            }
        }
        return isCandidate;
    }
}

package io.github.tesla.filter.support.plugins;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.tesla.filter.support.annnotation.AppKeyRequestPlugin;
import io.github.tesla.filter.utils.ClassUtils;

public class AppKeyRequestPluginMetadata extends RequestPluginMetadata {

    AppKeyRequestPluginMetadata(Class clz) {
        AppKeyRequestPlugin annotation = AnnotationUtils.findAnnotation(clz, AppKeyRequestPlugin.class);
        this.filterType = annotation.filterType();
        this.filterName = annotation.filterName();
        this.filterOrder = annotation.filterOrder();
        this.filterClass = clz;
        this.ignoreClassType = StringUtils.isBlank(annotation.ignoreClassType()) ? null : annotation.ignoreClassType();
        this.definitionClazz = annotation.definitionClazz();
    }

    public static AppKeyRequestPluginMetadata getMetadataByType(String filterType) {
        if (StringUtils.isBlank(filterType)) {
            return null;
        }
        Set<Class<?>> allClasses = ClassUtils.findAllClasses(packageName, AppKeyRequestPlugin.class);
        for (Class clz : allClasses) {
            if (filterType.equals(AnnotationUtils.findAnnotation(clz, AppKeyRequestPlugin.class).filterType())) {
                try {
                    return (AppKeyRequestPluginMetadata)META_CACHE.get(clz.getName(),
                        new Callable<AppKeyRequestPluginMetadata>() {

                            @Override
                            public AppKeyRequestPluginMetadata call() throws Exception {
                                return new AppKeyRequestPluginMetadata(clz);
                            }
                        });
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

}

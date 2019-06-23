package io.github.tesla.filter.support.plugins;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.support.annnotation.AppKeyRequestPlugin;
import io.github.tesla.filter.utils.ClassUtils;

@SuppressWarnings({"unchecked", "rawtypes"})
public class AppKeyRequestPluginMetadata extends RequestPluginMetadata {

    private static final long serialVersionUID = 1L;

    public AppKeyRequestPluginMetadata(Class<? extends AbstractRequestPlugin> clz) {
        super(clz);
        AppKeyRequestPlugin annotation = AnnotationUtils.findAnnotation(clz, AppKeyRequestPlugin.class);
        this.setFilterType(annotation.filterType());
        this.setFilterName(annotation.filterName());
        this.setFilterOrder(annotation.filterOrder());
        this.setIgnoreClassType(
            StringUtils.isBlank(annotation.ignoreClassType()) ? null : annotation.ignoreClassType());
        this.setDefinitionClazz(annotation.definitionClazz());
    }

    public static AppKeyRequestPluginMetadata getMetadataByType(String filterType) {
        if (StringUtils.isBlank(filterType)) {
            return null;
        }
        Set<Class<?>> allClasses = ClassUtils.findAllClasses(FILTER_SCAN_PACKAGE, AppKeyRequestPlugin.class);
        for (Class clz : allClasses) {
            if (filterType.equals(AnnotationUtils.findAnnotation(clz, AppKeyRequestPlugin.class).filterType())) {
                return (AppKeyRequestPluginMetadata)REQUESTPLUGINMETADATA_INSTANCE_CACHE.putIfAbsent(clz.getName(),
                    new AppKeyRequestPluginMetadata(clz));
            }
        }
        return null;
    }

}

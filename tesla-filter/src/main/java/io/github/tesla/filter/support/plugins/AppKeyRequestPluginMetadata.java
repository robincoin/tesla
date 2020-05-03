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
        Set<Class<?>> allClasses = ClassUtils.findAllClasses(FILTER_SCAN_PACKAGE, AppKeyRequestPlugin.class);
        for (Class clz : allClasses) {
            if (filterType.equals(AnnotationUtils.findAnnotation(clz, AppKeyRequestPlugin.class).filterType())) {
                final String claaName = clz.getName();
                if (REQUESTPLUGINMETADATA_INSTANCE_CACHE.containsKey(claaName)) {
                    return (AppKeyRequestPluginMetadata)REQUESTPLUGINMETADATA_INSTANCE_CACHE.get(claaName);
                } else {
                    AppKeyRequestPluginMetadata metadata = new AppKeyRequestPluginMetadata(clz);
                    REQUESTPLUGINMETADATA_INSTANCE_CACHE.put(claaName, metadata);
                    return metadata;
                }
            }
        }
        return null;
    }

}

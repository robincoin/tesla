package io.github.tesla.filter.support.plugins;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.tesla.filter.AbstractResponsePlugin;
import io.github.tesla.filter.support.annnotation.WafResponsePlugin;
import io.github.tesla.filter.utils.ClassUtils;

@SuppressWarnings({"unchecked", "rawtypes"})
public class WafResponsePluginMetadata extends ResponsePluginMetadata {

    private static final long serialVersionUID = 1L;

    public WafResponsePluginMetadata(Class<? extends AbstractResponsePlugin> clz) {
        super(clz);
        WafResponsePlugin annotation = AnnotationUtils.findAnnotation(clz, WafResponsePlugin.class);
        super.setFilterType(annotation.filterType());
        super.setFilterName(annotation.filterName());
        super.setFilterOrder(annotation.filterOrder());
        super.setIgnoreClassType(
            StringUtils.isBlank(annotation.ignoreClassType()) ? null : annotation.ignoreClassType());
        super.setDefinitionClazz(annotation.definitionClazz());
    }

    public static WafResponsePluginMetadata getMetadataByType(String filterType) {
        Set<Class<?>> allClasses = ClassUtils.findAllClasses(FILTER_SCAN_PACKAGE, WafResponsePlugin.class);
        for (Class clz : allClasses) {
            if (filterType.equals(AnnotationUtils.findAnnotation(clz, WafResponsePlugin.class).filterType())) {
                return (WafResponsePluginMetadata)RESPONSEPLUGINMETADATA_INSTANCE_CACHE.putIfAbsent(clz.getName(),
                    new WafResponsePluginMetadata(clz));
            }
        }
        return null;
    }

    public static String validate(String pluginType, String paramJson) {
        try {
            WafResponsePluginMetadata metadata = getMetadataByType(pluginType);
            if (metadata == null || metadata.getDefinitionClazz() == null) {
                return paramJson;
            }
            return metadata.getDefinitionClazz().getDeclaredConstructor().newInstance().validate(paramJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

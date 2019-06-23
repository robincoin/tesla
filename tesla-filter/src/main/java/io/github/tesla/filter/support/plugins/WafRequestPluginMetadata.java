package io.github.tesla.filter.support.plugins;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.support.annnotation.WafRequestPlugin;
import io.github.tesla.filter.utils.ClassUtils;

@SuppressWarnings({"unchecked", "rawtypes"})
public class WafRequestPluginMetadata extends RequestPluginMetadata {

    private static final long serialVersionUID = 1L;

    public WafRequestPluginMetadata(Class<? extends AbstractRequestPlugin> clz) {
        super(clz);
        WafRequestPlugin annotation = AnnotationUtils.findAnnotation(clz, WafRequestPlugin.class);
        this.setFilterType(annotation.filterType());
        this.setFilterName(annotation.filterName());
        this.setFilterOrder(annotation.filterOrder());
        this.setIgnoreClassType(
            StringUtils.isBlank(annotation.ignoreClassType()) ? null : annotation.ignoreClassType());
        this.setDefinitionClazz(annotation.definitionClazz());
    }

    public static WafRequestPluginMetadata getMetadataByType(String filterType) {
        Set<Class<?>> allClasses = ClassUtils.findAllClasses(FILTER_SCAN_PACKAGE, WafRequestPlugin.class);
        for (Class clz : allClasses) {
            if (filterType.equals(AnnotationUtils.findAnnotation(clz, WafRequestPlugin.class).filterType())) {
                return (WafRequestPluginMetadata)REQUESTPLUGINMETADATA_INSTANCE_CACHE.putIfAbsent(clz.getName(),
                    new WafRequestPluginMetadata(clz));
            }
        }
        return null;
    }

    public static String validate(String pluginType, String paramJson) {
        try {
            WafRequestPluginMetadata metadata = getMetadataByType(pluginType);
            if (metadata == null || metadata.getDefinitionClazz() == null) {
                return paramJson;
            }
            return metadata.getDefinitionClazz().getDeclaredConstructor().newInstance().validate(paramJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

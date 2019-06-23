package io.github.tesla.filter.support.plugins;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.support.annnotation.ServiceRequestPlugin;
import io.github.tesla.filter.utils.ClassUtils;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ServiceRequestPluginMetadata extends RequestPluginMetadata {

    private static final long serialVersionUID = 1L;

    public ServiceRequestPluginMetadata(Class<? extends AbstractRequestPlugin> clz) {
        super(clz);
        ServiceRequestPlugin annotation = AnnotationUtils.findAnnotation(clz, ServiceRequestPlugin.class);
        this.setFilterType(annotation.filterType());
        this.setFilterName(annotation.filterName());
        this.setFilterOrder(annotation.filterOrder());
        this.setIgnoreClassType(
            StringUtils.isBlank(annotation.ignoreClassType()) ? null : annotation.ignoreClassType());
        this.setDefinitionClazz(annotation.definitionClazz());
    }

    public static ServiceRequestPluginMetadata findAndCacheMetadataByType(String filterType) {
        Set<Class<?>> allClasses = ClassUtils.findAllClasses(FILTER_SCAN_PACKAGE, ServiceRequestPlugin.class);
        for (Class clz : allClasses) {
            if (filterType.equals(AnnotationUtils.findAnnotation(clz, ServiceRequestPlugin.class).filterType())) {
                final String claaName = clz.getName();
                if (REQUESTPLUGINMETADATA_INSTANCE_CACHE.containsKey(claaName)) {
                    return (ServiceRequestPluginMetadata)REQUESTPLUGINMETADATA_INSTANCE_CACHE.get(claaName);
                } else {
                    ServiceRequestPluginMetadata metadata = new ServiceRequestPluginMetadata(clz);
                    REQUESTPLUGINMETADATA_INSTANCE_CACHE.put(claaName, metadata);
                    return metadata;
                }
            }
        }
        return null;
    }

    public static String validate(String pluginType, String paramJson, ServiceDTO serviceDTO) {
        try {
            ServiceRequestPluginMetadata metadata = findAndCacheMetadataByType(pluginType);
            if (metadata == null) {
                return paramJson;
            } else {
                PluginDefinition pluginDefinition = metadata.getDefinitionClazz().newInstance();
                return pluginDefinition.validate(paramJson, serviceDTO);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

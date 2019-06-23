package io.github.tesla.filter.support.plugins;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.tesla.common.dto.EndpointDTO;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.AbstractResponsePlugin;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.support.annnotation.EndpointResponsePlugin;
import io.github.tesla.filter.utils.ClassUtils;

@SuppressWarnings({"unchecked", "rawtypes"})
public class EndpointResponsePluginMetadata extends ResponsePluginMetadata {

    private static final long serialVersionUID = 1L;

    public EndpointResponsePluginMetadata(Class<? extends AbstractResponsePlugin> clz) {
        super(clz);
        EndpointResponsePlugin annotation = AnnotationUtils.findAnnotation(clz, EndpointResponsePlugin.class);
        this.setFilterType(annotation.filterType());
        this.setFilterName(annotation.filterName());
        this.setFilterOrder(annotation.filterOrder());
        this.setIgnoreClassType(
            StringUtils.isBlank(annotation.ignoreClassType()) ? null : annotation.ignoreClassType());
        this.setDefinitionClazz(annotation.definitionClazz());
    }

    public static EndpointResponsePluginMetadata findAndCacheMetadataByType(String filterType) {
        Set<Class<?>> allClasses = ClassUtils.findAllClasses(FILTER_SCAN_PACKAGE, EndpointResponsePlugin.class);
        for (Class clz : allClasses) {
            if (filterType.equals(AnnotationUtils.findAnnotation(clz, EndpointResponsePlugin.class).filterType())) {
                final String claaName = clz.getName();
                if (RESPONSEPLUGINMETADATA_INSTANCE_CACHE.containsKey(claaName)) {
                    return (EndpointResponsePluginMetadata)RESPONSEPLUGINMETADATA_INSTANCE_CACHE.get(claaName);
                } else {
                    EndpointResponsePluginMetadata metadata = new EndpointResponsePluginMetadata(clz);
                    RESPONSEPLUGINMETADATA_INSTANCE_CACHE.put(claaName, metadata);
                    return metadata;
                }
            }
        }
        return null;
    }

    public static String validate(String pluginType, String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        try {
            EndpointResponsePluginMetadata metadata = findAndCacheMetadataByType(pluginType);
            if (metadata == null) {
                return paramJson;
            } else {
                PluginDefinition pluginDefinition = metadata.getDefinitionClazz().newInstance();
                return pluginDefinition.validate(paramJson, serviceDTO, endpointDTO);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

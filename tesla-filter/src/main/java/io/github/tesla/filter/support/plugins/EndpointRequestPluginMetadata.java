package io.github.tesla.filter.support.plugins;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.tesla.common.dto.EndpointDTO;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.utils.ClassUtils;

@SuppressWarnings({"unchecked", "rawtypes"})
public class EndpointRequestPluginMetadata extends RequestPluginMetadata {

    private static final long serialVersionUID = 1L;

    EndpointRequestPluginMetadata(Class<? extends AbstractRequestPlugin> clz) {
        super(clz);
        EndpointRequestPlugin annotation = AnnotationUtils.findAnnotation(clz, EndpointRequestPlugin.class);
        this.setFilterType(annotation.filterType());
        this.setFilterName(annotation.filterName());
        this.setFilterOrder(annotation.filterOrder());
        this.setIgnoreClassType(
            StringUtils.isBlank(annotation.ignoreClassType()) ? null : annotation.ignoreClassType());
        this.setDefinitionClazz(annotation.definitionClazz());
    }

    public static EndpointRequestPluginMetadata findAndCacheMetadataByType(String filterType) {
        Set<Class<?>> allClasses =
            ClassUtils.findAllClasses(FilterMetadata.FILTER_SCAN_PACKAGE, EndpointRequestPlugin.class);
        for (Class clz : allClasses) {
            if (filterType.equals(AnnotationUtils.findAnnotation(clz, EndpointRequestPlugin.class).filterType())) {
                return (EndpointRequestPluginMetadata)REQUESTPLUGINMETADATA_INSTANCE_CACHE.putIfAbsent(clz.getName(),
                    new EndpointRequestPluginMetadata(clz));
            }
        }
        return null;
    }

    public static EndpointRequestPluginMetadata findMetadataByType(String filterType) {
        Set<Class<?>> allClasses =
            ClassUtils.findAllClasses(FilterMetadata.FILTER_SCAN_PACKAGE, EndpointRequestPlugin.class);
        for (Class clz : allClasses) {
            if (filterType.equals(AnnotationUtils.findAnnotation(clz, EndpointRequestPlugin.class).filterType())) {
                return new EndpointRequestPluginMetadata(clz);
            }
        }
        return null;
    }

    public static String validate(String pluginType, String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        try {
            EndpointRequestPluginMetadata metadata = findMetadataByType(pluginType);
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

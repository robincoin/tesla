package io.github.tesla.filter.support.plugins;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.tesla.common.dto.EndpointDTO;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.AbstractResponsePlugin;
import io.github.tesla.filter.support.annnotation.EndpointResponsePlugin;
import io.github.tesla.filter.utils.ClassUtils;

public class EndpointResponsePluginMetadata extends ResponsePluginMetadata {

    private static final long serialVersionUID = 1L;

    EndpointResponsePluginMetadata(Class<? extends AbstractResponsePlugin> clz) {
        EndpointResponsePlugin annotation = AnnotationUtils.findAnnotation(clz, EndpointResponsePlugin.class);
        this.filterType = annotation.filterType();
        this.filterName = annotation.filterName();
        this.filterOrder = annotation.filterOrder();
        this.filterClass = clz;
        this.ignoreClassType = StringUtils.isBlank(annotation.ignoreClassType()) ? null : annotation.ignoreClassType();
        this.definitionClazz = annotation.definitionClazz();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static EndpointResponsePluginMetadata getMetadataByType(String filterType) {
        if (StringUtils.isBlank(filterType)) {
            return null;
        }
        Set<Class<?>> allClasses = ClassUtils.findAllClasses(packageName, EndpointResponsePlugin.class);
        for (Class clz : allClasses) {
            if (filterType.equals(AnnotationUtils.findAnnotation(clz, EndpointResponsePlugin.class).filterType())) {
                return (EndpointResponsePluginMetadata)RESPONSEPLUGINMETADATA_INSTANCE_CACHE.putIfAbsent(clz.getName(),
                    new EndpointResponsePluginMetadata(clz));
            }
        }
        return null;
    }

    public static String validate(String pluginType, String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        try {
            EndpointResponsePluginMetadata metadata = getMetadataByType(pluginType);
            if (metadata == null || metadata.definitionClazz == null) {
                return paramJson;
            }
            return metadata.definitionClazz.getDeclaredConstructor().newInstance().validate(paramJson, serviceDTO,
                endpointDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

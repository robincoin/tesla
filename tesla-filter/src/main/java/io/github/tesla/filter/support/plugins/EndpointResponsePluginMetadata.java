package io.github.tesla.filter.support.plugins;

import java.lang.ref.SoftReference;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.tesla.common.dto.EndpointDTO;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.support.annnotation.EndpointResponsePlugin;
import io.github.tesla.filter.utils.ClassUtils;

public class EndpointResponsePluginMetadata extends ResponsePluginMetadata {

    private static SoftReference<EndpointResponsePluginMetadata> ENDPOINTRESPONSEPLUGINMETADATAREFERENCE;

    EndpointResponsePluginMetadata(Class clz) {
        EndpointResponsePlugin annotation = AnnotationUtils.findAnnotation(clz, EndpointResponsePlugin.class);
        this.filterType = annotation.filterType();
        this.filterName = annotation.filterName();
        this.filterOrder = annotation.filterOrder();
        this.filterClass = clz;
        this.ignoreClassType = StringUtils.isBlank(annotation.ignoreClassType()) ? null : annotation.ignoreClassType();
        this.definitionClazz = annotation.definitionClazz();
        ENDPOINTRESPONSEPLUGINMETADATAREFERENCE = new SoftReference<EndpointResponsePluginMetadata>(this);
    }

    public static EndpointResponsePluginMetadata getMetadataByType(String filterType) {
        if (StringUtils.isBlank(filterType)) {
            return null;
        }
        Set<Class<?>> allClasses = ClassUtils.findAllClasses(packageName, EndpointResponsePlugin.class);
        for (Class clz : allClasses) {
            if (filterType.equals(AnnotationUtils.findAnnotation(clz, EndpointResponsePlugin.class).filterType())) {
                return ENDPOINTRESPONSEPLUGINMETADATAREFERENCE.get() != null
                    ? ENDPOINTRESPONSEPLUGINMETADATAREFERENCE.get() : new EndpointResponsePluginMetadata(clz);
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

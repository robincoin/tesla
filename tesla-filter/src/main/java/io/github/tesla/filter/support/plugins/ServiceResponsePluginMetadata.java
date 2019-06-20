package io.github.tesla.filter.support.plugins;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.AbstractResponsePlugin;
import io.github.tesla.filter.support.annnotation.ServiceResponsePlugin;
import io.github.tesla.filter.utils.ClassUtils;

public class ServiceResponsePluginMetadata extends ResponsePluginMetadata {

    private static final long serialVersionUID = 1L;

    ServiceResponsePluginMetadata(Class<? extends AbstractResponsePlugin> clz) {
        ServiceResponsePlugin annotation = AnnotationUtils.findAnnotation(clz, ServiceResponsePlugin.class);
        this.filterType = annotation.filterType();
        this.filterName = annotation.filterName();
        this.filterOrder = annotation.filterOrder();
        this.filterClass = clz;
        this.ignoreClassType = StringUtils.isBlank(annotation.ignoreClassType()) ? null : annotation.ignoreClassType();
        this.definitionClazz = annotation.definitionClazz();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ServiceResponsePluginMetadata getMetadataByType(String filterType) {
        if (StringUtils.isBlank(filterType)) {
            return null;
        }
        Set<Class<?>> allClasses = ClassUtils.findAllClasses(packageName, ServiceResponsePlugin.class);
        for (Class clz : allClasses) {
            if (filterType.equals(AnnotationUtils.findAnnotation(clz, ServiceResponsePlugin.class).filterType())) {
                return (ServiceResponsePluginMetadata)RESPONSEPLUGINMETADATA_INSTANCE_CACHE.putIfAbsent(clz.getName(),
                    new ServiceResponsePluginMetadata(clz));
            }
        }
        return null;
    }

    public static String validate(String pluginType, String paramJson, ServiceDTO serviceDTO) {
        try {
            ServiceResponsePluginMetadata metadata = getMetadataByType(pluginType);
            if (metadata == null || metadata.definitionClazz == null) {
                return paramJson;
            }
            return metadata.definitionClazz.getDeclaredConstructor().newInstance().validate(paramJson, serviceDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

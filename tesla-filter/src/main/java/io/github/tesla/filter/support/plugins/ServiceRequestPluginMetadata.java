package io.github.tesla.filter.support.plugins;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.support.annnotation.ServiceRequestPlugin;
import io.github.tesla.filter.utils.ClassUtils;

public class ServiceRequestPluginMetadata extends RequestPluginMetadata {

    private static final long serialVersionUID = 1L;

    ServiceRequestPluginMetadata(Class<? extends AbstractRequestPlugin> clz) {
        ServiceRequestPlugin annotation = AnnotationUtils.findAnnotation(clz, ServiceRequestPlugin.class);
        this.filterType = annotation.filterType();
        this.filterName = annotation.filterName();
        this.filterOrder = annotation.filterOrder();
        this.filterClass = clz;
        this.ignoreClassType = StringUtils.isBlank(annotation.ignoreClassType()) ? null : annotation.ignoreClassType();
        this.definitionClazz = annotation.definitionClazz();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ServiceRequestPluginMetadata getMetadataByType(String filterType) {
        if (StringUtils.isBlank(filterType)) {
            return null;
        }
        Set<Class<?>> allClasses = ClassUtils.findAllClasses(packageName, ServiceRequestPlugin.class);
        for (Class clz : allClasses) {
            if (filterType.equals(AnnotationUtils.findAnnotation(clz, ServiceRequestPlugin.class).filterType())) {
                return (ServiceRequestPluginMetadata)REQUESTPLUGINMETADATA_INSTANCE_CACHE.putIfAbsent(clz.getName(),
                    new ServiceRequestPluginMetadata(clz));
            }
        }
        return null;
    }

    public static String validate(String pluginType, String paramJson, ServiceDTO serviceDTO) {
        try {
            ServiceRequestPluginMetadata metadata = getMetadataByType(pluginType);
            if (metadata == null || metadata.definitionClazz == null) {
                return paramJson;
            }
            return metadata.definitionClazz.getDeclaredConstructor().newInstance().validate(paramJson, serviceDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

package io.github.tesla.filter.support.plugins;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.support.annnotation.ServiceRequestPlugin;
import io.github.tesla.filter.utils.ClassUtils;

public class ServiceRequestPluginMetadata extends RequestPluginMetadata {

    ServiceRequestPluginMetadata(Class clz) {
        ServiceRequestPlugin annotation = AnnotationUtils.findAnnotation(clz, ServiceRequestPlugin.class);
        this.filterType = annotation.filterType();
        this.filterName = annotation.filterName();
        this.filterOrder = annotation.filterOrder();
        this.filterClass = clz;
        this.ignoreClassType = StringUtils.isBlank(annotation.ignoreClassType()) ? null : annotation.ignoreClassType();
        this.definitionClazz = annotation.definitionClazz();
    }

    public static ServiceRequestPluginMetadata getMetadataByType(String filterType) {
        if (StringUtils.isBlank(filterType)) {
            return null;
        }
        Set<Class<?>> allClasses = ClassUtils.findAllClasses(packageName, ServiceRequestPlugin.class);
        for (Class clz : allClasses) {
            if (filterType.equals(AnnotationUtils.findAnnotation(clz, ServiceRequestPlugin.class).filterType())) {
                try {
                    return (ServiceRequestPluginMetadata)META_CACHE.get(clz.getName(),
                        new Callable<ServiceRequestPluginMetadata>() {

                            @Override
                            public ServiceRequestPluginMetadata call() throws Exception {
                                return new ServiceRequestPluginMetadata(clz);
                            }
                        });
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
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

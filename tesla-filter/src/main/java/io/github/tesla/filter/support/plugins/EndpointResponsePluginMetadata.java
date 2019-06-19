package io.github.tesla.filter.support.plugins;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.tesla.common.dto.EndpointDTO;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.support.annnotation.EndpointResponsePlugin;
import io.github.tesla.filter.utils.ClassUtils;

public class EndpointResponsePluginMetadata extends ResponsePluginMetadata {

    EndpointResponsePluginMetadata(Class clz) {
        EndpointResponsePlugin annotation = AnnotationUtils.findAnnotation(clz, EndpointResponsePlugin.class);
        this.filterType = annotation.filterType();
        this.filterName = annotation.filterName();
        this.filterOrder = annotation.filterOrder();
        this.filterClass = clz;
        this.ignoreClassType = StringUtils.isBlank(annotation.ignoreClassType()) ? null : annotation.ignoreClassType();
        this.definitionClazz = annotation.definitionClazz();
    }

    public static EndpointResponsePluginMetadata getMetadataByType(String filterType) {
        if (StringUtils.isBlank(filterType)) {
            return null;
        }
        Set<Class<?>> allClasses = ClassUtils.findAllClasses(packageName, EndpointResponsePlugin.class);
        for (Class clz : allClasses) {
            if (filterType.equals(AnnotationUtils.findAnnotation(clz, EndpointResponsePlugin.class).filterType())) {
                try {
                    return (EndpointResponsePluginMetadata)META_CACHE.get(clz,
                        new Callable<EndpointResponsePluginMetadata>() {

                            @Override
                            public EndpointResponsePluginMetadata call() throws Exception {
                                return new EndpointResponsePluginMetadata(clz);
                            }
                        });
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
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

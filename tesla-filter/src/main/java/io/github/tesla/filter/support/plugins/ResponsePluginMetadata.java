package io.github.tesla.filter.support.plugins;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import io.github.tesla.filter.AbstractResponsePlugin;

@SuppressWarnings({"unchecked"})
public class ResponsePluginMetadata extends FilterMetadata {

    private static final long serialVersionUID = 1L;

    private final Class<? extends AbstractResponsePlugin> filterClass;

    private static final Map<String, Object> RESPONSEPLUGIN_INSTANCE_CACHE =
        Collections.synchronizedMap(new WeakHashMap<String, Object>());

    protected static final Map<String, ResponsePluginMetadata> RESPONSEPLUGINMETADATA_INSTANCE_CACHE =
        Collections.synchronizedMap(new WeakHashMap<String, ResponsePluginMetadata>());

    public ResponsePluginMetadata(Class<? extends AbstractResponsePlugin> filterClass) {
        this.filterClass = filterClass;
    }

    public Class<? extends AbstractResponsePlugin> getFilterClass() {
        return filterClass;
    }

    public <T extends AbstractResponsePlugin> T getInstance() throws Exception {
        final Class<? extends AbstractResponsePlugin> clazz = this.filterClass;
        final String clazzName = clazz.getName();
        if (RESPONSEPLUGIN_INSTANCE_CACHE.containsKey(clazzName)) {
            return (T)RESPONSEPLUGIN_INSTANCE_CACHE.get(clazzName);
        } else {
            T clazzInstance = (T)clazz.newInstance();
            RESPONSEPLUGIN_INSTANCE_CACHE.put(clazzName, clazzInstance);
            return clazzInstance;
        }
    }
}

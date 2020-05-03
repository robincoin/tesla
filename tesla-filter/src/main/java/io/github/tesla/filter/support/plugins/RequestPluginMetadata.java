package io.github.tesla.filter.support.plugins;

import java.util.Map;

import com.google.common.collect.Maps;

import io.github.tesla.filter.AbstractRequestPlugin;

@SuppressWarnings({"unchecked"})
public class RequestPluginMetadata extends FilterMetadata {

    private static final long serialVersionUID = 1L;

    private final Class<? extends AbstractRequestPlugin> filterClass;

    private static final Map<String, Object> REQUESTPLUGIN_INSTANCE_CACHE = Maps.newHashMap();

    protected static final Map<String, RequestPluginMetadata> REQUESTPLUGINMETADATA_INSTANCE_CACHE = Maps.newHashMap();

    public RequestPluginMetadata(Class<? extends AbstractRequestPlugin> filterClass) {
        this.filterClass = filterClass;
    }

    public Class<? extends AbstractRequestPlugin> getFilterClass() {
        return filterClass;
    }

    public <T extends AbstractRequestPlugin> T getInstance() throws Exception {
        final Class<? extends AbstractRequestPlugin> clazz = this.filterClass;
        final String clazzName = clazz.getName();
        if (REQUESTPLUGIN_INSTANCE_CACHE.containsKey(clazzName)) {
            return (T)REQUESTPLUGIN_INSTANCE_CACHE.get(clazzName);
        } else {
            T clazzInstance = (T)clazz.newInstance();
            REQUESTPLUGIN_INSTANCE_CACHE.put(clazzName, clazzInstance);
            return clazzInstance;
        }
    }
}

package io.github.tesla.filter.support.plugins;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import io.github.tesla.filter.AbstractRequestPlugin;

@SuppressWarnings({"unchecked"})
public class RequestPluginMetadata extends FilterMetadata {

    private static final long serialVersionUID = 1L;

    private final Class<? extends AbstractRequestPlugin> filterClass;

    private static final Map<String, Object> REQUESTPLUGIN_INSTANCE_CACHE =
        Collections.synchronizedMap(new WeakHashMap<String, Object>());

    protected static final Map<String, RequestPluginMetadata> REQUESTPLUGINMETADATA_INSTANCE_CACHE =
        Collections.synchronizedMap(new WeakHashMap<String, RequestPluginMetadata>());

    public RequestPluginMetadata(Class<? extends AbstractRequestPlugin> filterClass) {
        this.filterClass = filterClass;
    }

    public Class<? extends AbstractRequestPlugin> getFilterClass() {
        return filterClass;
    }

    public <T extends AbstractRequestPlugin> T getInstance() throws Exception {
        return (T)REQUESTPLUGIN_INSTANCE_CACHE.putIfAbsent(getFilterClass().getName(),
            getFilterClass().getDeclaredConstructor().newInstance());
    }
}

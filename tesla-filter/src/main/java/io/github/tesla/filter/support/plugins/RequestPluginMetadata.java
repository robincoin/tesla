package io.github.tesla.filter.support.plugins;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import io.github.tesla.filter.AbstractRequestPlugin;

public class RequestPluginMetadata extends FilterMetadata {

    private static final long serialVersionUID = 1L;

    protected Class<? extends AbstractRequestPlugin> filterClass;

    private static final Map<String, Object> INSTANCE_CACHE =
        Collections.synchronizedMap(new WeakHashMap<String, Object>());

    protected static final Map<String, RequestPluginMetadata> META_CACHE =
        Collections.synchronizedMap(new WeakHashMap<String, RequestPluginMetadata>());

    public Class<? extends AbstractRequestPlugin> getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(Class<? extends AbstractRequestPlugin> filterClass) {
        this.filterClass = filterClass;
    }

    @SuppressWarnings({"unchecked"})
    public <T extends AbstractRequestPlugin> T getInstance() throws Exception {
        return (T)INSTANCE_CACHE.putIfAbsent(getFilterClass().getName(),
            getFilterClass().getDeclaredConstructor().newInstance());
    }
}

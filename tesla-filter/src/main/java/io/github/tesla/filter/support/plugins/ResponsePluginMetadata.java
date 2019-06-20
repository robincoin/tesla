package io.github.tesla.filter.support.plugins;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import io.github.tesla.filter.AbstractResponsePlugin;

public class ResponsePluginMetadata extends FilterMetadata {

    private static final long serialVersionUID = 1L;

    protected Class<? extends AbstractResponsePlugin> filterClass;

    private static final Map<String, Object> INSTANCE_CACHE =
        Collections.synchronizedMap(new WeakHashMap<String, Object>());

    protected static final Map<String, ResponsePluginMetadata> META_CACHE =
        Collections.synchronizedMap(new WeakHashMap<String, ResponsePluginMetadata>());

    public Class<? extends AbstractResponsePlugin> getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(Class<? extends AbstractResponsePlugin> filterClass) {
        this.filterClass = filterClass;
    }

    @SuppressWarnings({"unchecked"})
    public <T extends ResponsePluginMetadata> T getInstance() throws Exception {

        return (T)INSTANCE_CACHE.putIfAbsent(getFilterClass().getName(),
            getFilterClass().getDeclaredConstructor().newInstance());
    }
}

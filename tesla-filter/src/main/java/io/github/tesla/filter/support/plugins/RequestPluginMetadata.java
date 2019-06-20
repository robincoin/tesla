package io.github.tesla.filter.support.plugins;

import java.util.concurrent.Callable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.github.tesla.filter.AbstractRequestPlugin;

public class RequestPluginMetadata extends FilterMetadata {

    protected Class<? extends AbstractRequestPlugin> filterClass;

    private static final Cache<String, Object> INSTANCE_CACHE =
        CacheBuilder.newBuilder().weakKeys().weakValues().build();

    protected static final Cache<String, RequestPluginMetadata> META_CACHE =
        CacheBuilder.newBuilder().weakKeys().weakValues().build();

    public Class<? extends AbstractRequestPlugin> getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(Class<? extends AbstractRequestPlugin> filterClass) {
        this.filterClass = filterClass;
    }

    public <T> T getInstance() throws Exception {
        return (T)INSTANCE_CACHE.get(filterClass.getName(), new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                return getFilterClass().getDeclaredConstructor().newInstance();
            }
        });

    }
}

package io.github.tesla.filter.support.plugins;

import java.util.concurrent.Callable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.github.tesla.filter.AbstractResponsePlugin;

public class ResponsePluginMetadata extends FilterMetadata {

    protected Class<? extends AbstractResponsePlugin> filterClass;

    private static final Cache<Class<? extends AbstractResponsePlugin>, Object> INSTANCE_CACHE =
        CacheBuilder.newBuilder().weakKeys().weakValues().build();

    protected static final Cache<Class<? extends AbstractResponsePlugin>, ResponsePluginMetadata> META_CACHE =
        CacheBuilder.newBuilder().weakKeys().weakValues().build();

    public Class<? extends AbstractResponsePlugin> getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(Class<? extends AbstractResponsePlugin> filterClass) {
        this.filterClass = filterClass;
    }

    public <T> T getInstance() throws Exception {

        return (T)INSTANCE_CACHE.get(filterClass, new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                return getFilterClass().getDeclaredConstructor().newInstance();
            }
        });
    }
}

package io.github.tesla.gateway.excutor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import io.github.tesla.filter.support.enums.HttpMethodEnum;
import io.github.tesla.filter.utils.AntMatchUtil;

public class ServiceExecutor implements Comparable<ServiceExecutor>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<String, EndpointExecutor> MATCHENDPOINTEXECUTOR =
        new WeakHashMap<String, EndpointExecutor>();

    private String servicePrefix;

    private List<EndpointExecutor> endPointDefinitionList;

    private ServiceRouterExecutor routerCache;

    @Override
    public int compareTo(ServiceExecutor o) {
        return o.servicePrefix.length() - this.servicePrefix.length();
    }

    public List<EndpointExecutor> getEndPointDefinitionList() {
        return endPointDefinitionList;
    }

    private EndpointExecutor findAndCacheMatchEndpointExecutor(String uri, String method) {
        final String cacheKey = uri + method;
        if (MATCHENDPOINTEXECUTOR.containsKey(cacheKey)) {
            return MATCHENDPOINTEXECUTOR.get(cacheKey);
        } else {
            EndpointExecutor hitendpointExecutor = null;
            for (EndpointExecutor endpointExecutor : getEndPointDefinitionList()) {
                String path = AntMatchUtil.replacePrefix(uri, servicePrefix, "");
                if (AntMatchUtil.match(endpointExecutor.getEndPointPath(), path)
                    && HttpMethodEnum.match(endpointExecutor.getEndPointMethod(), method)) {
                    hitendpointExecutor = endpointExecutor;
                    break;
                }
            }
            if (hitendpointExecutor != null) {
                MATCHENDPOINTEXECUTOR.put(cacheKey, hitendpointExecutor);
            }
            return hitendpointExecutor;
        }

    }

    public ServiceRouterExecutor getRouterCache() {
        return routerCache;
    }

    public String getServicePrefix() {
        return servicePrefix;
    }

    public List<ServiceRequestPluginExecutor> matchAndGetRequestFiltes(String uri, String method) {
        EndpointExecutor endpointExector = findAndCacheMatchEndpointExecutor(uri, method);
        return endpointExector != null ? endpointExector.getRequestFiltersList() : null;
    }

    public List<ServiceResponsePluginExecutor> matchAndGetResponseFiltes(String uri, String method) {
        EndpointExecutor endpointExector = findAndCacheMatchEndpointExecutor(uri, method);
        return endpointExector != null ? endpointExector.getResponseFiltersList() : Collections.emptyList();
    }

    public void setEndPointDefinitionList(List<EndpointExecutor> endPointCacheList) {
        this.endPointDefinitionList = endPointCacheList;
        MATCHENDPOINTEXECUTOR.clear();
    }

    public void setRouterCache(ServiceRouterExecutor routerCache) {
        this.routerCache = routerCache;
    }

    public void setServicePrefix(String servicePrefix) {
        this.servicePrefix = servicePrefix;
        MATCHENDPOINTEXECUTOR.clear();
    }
}

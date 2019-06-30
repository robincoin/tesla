package io.github.tesla.gateway.excutor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import io.github.tesla.filter.support.enums.HttpMethodEnum;
import io.github.tesla.filter.utils.AntMatchUtil;

public class ServiceExecutor implements Comparable<ServiceExecutor>, Serializable {

    private static final long serialVersionUID = 1L;

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

    private EndpointExecutor getMatchEndpointExecutor(String uri, String method) {
        for (EndpointExecutor endpointExecutor : getEndPointDefinitionList()) {
            String path = AntMatchUtil.replacePrefix(uri, servicePrefix, "");
            if (AntMatchUtil.match(endpointExecutor.getEndPointPath(), path)
                && HttpMethodEnum.match(endpointExecutor.getEndPointMethod(), method)) {
                return endpointExecutor;
            }
        }
        return null;
    }

    public ServiceRouterExecutor getRouterCache() {
        return routerCache;
    }

    public String getServicePrefix() {
        return servicePrefix;
    }

    public List<ServiceRequestPluginExecutor> matchAndGetRequestFiltes(String uri, String method) {
        EndpointExecutor endpointExector = getMatchEndpointExecutor(uri, method);
        return endpointExector != null ? endpointExector.getRequestFiltersList() : null;
    }

    public List<ServiceResponsePluginExecutor> matchAndGetResponseFiltes(String uri, String method) {
        EndpointExecutor endpointExector = getMatchEndpointExecutor(uri, method);
        return endpointExector != null ? endpointExector.getResponseFiltersList() : Collections.emptyList();
    }

    public void setEndPointDefinitionList(List<EndpointExecutor> endPointCacheList) {
        this.endPointDefinitionList = endPointCacheList;
    }

    public void setRouterCache(ServiceRouterExecutor routerCache) {
        this.routerCache = routerCache;
    }

    public void setServicePrefix(String servicePrefix) {
        this.servicePrefix = servicePrefix;
    }
}

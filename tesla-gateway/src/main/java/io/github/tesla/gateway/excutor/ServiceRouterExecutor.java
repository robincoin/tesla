package io.github.tesla.gateway.excutor;

import java.io.Serializable;

public class ServiceRouterExecutor implements Serializable {
    private static final long serialVersionUID = 1L;

    private String routeType;

    private String paramJson;

    public String getParamJson() {
        return paramJson;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }
}

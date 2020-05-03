package io.github.tesla.common.domain;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("gateway_endpoint_plugin")
public class GatewayEndpointPluginDO extends GatewayCommonPluginDO {

    private static final long serialVersionUID = -5774515519348499623L;
    protected String endpointId;

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }
}

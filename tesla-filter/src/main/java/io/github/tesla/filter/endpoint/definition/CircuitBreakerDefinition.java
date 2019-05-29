package io.github.tesla.filter.endpoint.definition;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.utils.JsonUtils;

public class CircuitBreakerDefinition extends PluginDefinition {

    private String failRateForClose;

    private Integer idleTimeForOpen;

    private String passRateForHalfOpen;

    private Integer failNumForHalfOpen;

    private String fallback;

    public String getFailRateForClose() {
        return failRateForClose;
    }

    public void setFailRateForClose(String failRateForClose) {
        this.failRateForClose = failRateForClose;
    }

    public Integer getIdleTimeForOpen() {
        return idleTimeForOpen;
    }

    public void setIdleTimeForOpen(Integer idleTimeForOpen) {
        this.idleTimeForOpen = idleTimeForOpen;
    }

    public String getPassRateForHalfOpen() {
        return passRateForHalfOpen;
    }

    public void setPassRateForHalfOpen(String passRateForHalfOpen) {
        this.passRateForHalfOpen = passRateForHalfOpen;
    }

    public Integer getFailNumForHalfOpen() {
        return failNumForHalfOpen;
    }

    public void setFailNumForHalfOpen(Integer failNumForHalfOpen) {
        this.failNumForHalfOpen = failNumForHalfOpen;
    }

    public String getFallback() {
        return fallback;
    }

    public void setFallback(String fallback) {
        this.fallback = fallback;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO) {
        CircuitBreakerDefinition definition = JsonUtils.fromJson(paramJson, CircuitBreakerDefinition.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getFailRateForClose()), "默认多少分钟内，失败多少次进入打开状态");
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getPassRateForHalfOpen()), "默认多少分钟内，放多少次请求去试探");
        Preconditions.checkArgument(definition.getIdleTimeForOpen() != null, "在熔断器打开的情况下，熔断多少秒进入半开状态");
        Preconditions.checkArgument(definition.getFailNumForHalfOpen() != null,
            "在熔断器半开的情况下, 试探期间，如果有超过多少次失败的，重新进入熔断打开状态，否者进入熔断关闭状态");
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getFallback()), "熔断情况下返回的Mock数据");
        return paramJson;
    }
}

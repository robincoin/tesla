package io.github.tesla.filter.endpoint.definition;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.service.definition.PluginDefinition;

public class CircuitBreakerDefinition extends PluginDefinition {

    // 失败率阈值
    private Integer failureRateThreshold;

    // 用来指定断路器从OPEN到HALF_OPEN状态等待的时长
    private Integer waitDurationInOpenState;

    // 设置当断路器处于HALF_OPEN状态下的ringbuffer的大小，它存储了最近一段时间请求的成功失败状态
    private Integer ringBufferSizeInHalfOpenState;

    // 设置当断路器处于CLOSED状态下的ringbuffer的大小，它存储了最近一段时间请求的成功失败状态
    private Integer ringBufferSizeInClosedState;

    // 触发熔断后降级
    private String fallback;

    public Integer getFailureRateThreshold() {
        return failureRateThreshold;
    }

    public void setFailureRateThreshold(Integer failureRateThreshold) {
        this.failureRateThreshold = failureRateThreshold;
    }

    public Integer getWaitDurationInOpenState() {
        return waitDurationInOpenState;
    }

    public void setWaitDurationInOpenState(Integer waitDurationInOpenState) {
        this.waitDurationInOpenState = waitDurationInOpenState;
    }

    public Integer getRingBufferSizeInHalfOpenState() {
        return ringBufferSizeInHalfOpenState;
    }

    public void setRingBufferSizeInHalfOpenState(Integer ringBufferSizeInHalfOpenState) {
        this.ringBufferSizeInHalfOpenState = ringBufferSizeInHalfOpenState;
    }

    public Integer getRingBufferSizeInClosedState() {
        return ringBufferSizeInClosedState;
    }

    public void setRingBufferSizeInClosedState(Integer ringBufferSizeInClosedState) {
        this.ringBufferSizeInClosedState = ringBufferSizeInClosedState;
    }

    public String getFallback() {
        return fallback;
    }

    public void setFallback(String fallback) {
        this.fallback = fallback;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO) {
        return paramJson;
    }

}

package io.github.tesla.filter.endpoint.definition;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.service.definition.PluginDefinition;

public class CircuitBreakerDefinition extends PluginDefinition {

	private Integer failureRateThreshold;

	private Integer waitDurationInOpenState;

	private Integer ringBufferSizeInHalfOpenState;

	private Integer ringBufferSizeInClosedState;

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

package io.github.tesla.filter.endpoint.plugin.request;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.endpoint.definition.CircuitBreakerDefinition;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;

@EndpointRequestPlugin(filterType = "CircuitBreakerRequestPlugin", definitionClazz = CircuitBreakerDefinition.class, filterOrder = 13, filterName = "熔断插件")
public class CircuitBreakerRequestPlugin extends AbstractRequestPlugin {

	private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerRequestPlugin.class);

	@Override
	public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
			Object filterParam) {
		CircuitBreakerDefinition definition = JsonUtils.json2Definition(filterParam, CircuitBreakerDefinition.class);
		if (definition == null) {
			return null;
		}
		String uri = servletRequest.getRequestURI();
		Integer failureRateThreshold = definition.getFailureRateThreshold();
		Integer waitDurationInOpenState = definition.getWaitDurationInOpenState();
		Integer ringBufferSizeInHalfOpenState = definition.getRingBufferSizeInHalfOpenState();
		Integer ringBufferSizeInClosedState = definition.getRingBufferSizeInClosedState();
		CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()//
				.failureRateThreshold(failureRateThreshold)// 失败率阈值
				.waitDurationInOpenState(Duration.ofMillis(waitDurationInOpenState))// 用来指定断路器从OPEN到HALF_OPEN状态等待的时长
				.ringBufferSizeInHalfOpenState(ringBufferSizeInHalfOpenState)// 设置当断路器处于HALF_OPEN状态下的ring
																				// buffer的大小，它存储了最近一段时间请求的成功失败状态
				.ringBufferSizeInClosedState(ringBufferSizeInClosedState)// 设置当断路器处于CLOSED状态下的ring
																			// buffer的大小，它存储了最近一段时间请求的成功失败状态
				.enableAutomaticTransitionFromOpenToHalfOpen()// 当waitDurationInOpenState时间一过，是否自动从OPEN切换到HALF_OPEN
				.build();
		CircuitBreaker circuitBreaker = CircuitBreakerRegistry.ofDefaults().circuitBreaker(uri, circuitBreakerConfig);
		boolean acquirePermission = circuitBreaker.tryAcquirePermission();
		long start = System.nanoTime();
		servletRequest.setAttribute("_CircuitBreakerStart", start);
		if (acquirePermission) {
			return null;
		} else {
			logger.warn("circuitBreaker is triggered, will return fallback data,the data is:{}",
					definition.getFallback());
			HttpResponse response = PluginUtil.createResponse(HttpResponseStatus.OK, servletRequest.getNettyRequest(),
					definition.getFallback());
			HttpUtil.setKeepAlive(response, false);
			return response;
		}
	}

}

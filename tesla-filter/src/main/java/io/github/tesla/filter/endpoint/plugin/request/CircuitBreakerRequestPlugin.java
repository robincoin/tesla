package io.github.tesla.filter.endpoint.plugin.request;

import java.util.Map;

import com.google.common.collect.Maps;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.endpoint.definition.CircuitBreakerDefinition;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.support.circuitbreaker.CircuitBreaker;
import io.github.tesla.filter.support.circuitbreaker.LocalCircuitBreaker;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;

@EndpointRequestPlugin(filterType = "CircuitBreakerRequestPlugin", definitionClazz = CircuitBreakerDefinition.class,
    filterOrder = 13, filterName = "熔断插件")
public class CircuitBreakerRequestPlugin extends AbstractRequestPlugin {

    private static final Map<String, CircuitBreaker> CIRCUITBREAKER_CACHE = Maps.newConcurrentMap();

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        CircuitBreakerDefinition definition = JsonUtils.json2Definition(filterParam, CircuitBreakerDefinition.class);
        if (definition == null) {
            return null;
        }
        String uri = servletRequest.getRequestURI();
        CircuitBreaker circuitBreaker = null;
        final String failRateForClose = definition.getFailRateForClose();
        final int idleTimeForOpen = definition.getIdleTimeForOpen();
        final String passRateForHalfOpen = definition.getPassRateForHalfOpen();
        final int failNumForHalfOpen = definition.getFailNumForHalfOpen();
        if (CIRCUITBREAKER_CACHE.containsKey(uri)) {
            circuitBreaker = CIRCUITBREAKER_CACHE.get(uri);
            circuitBreaker.reset(failRateForClose, idleTimeForOpen, passRateForHalfOpen, failNumForHalfOpen);
        } else {
            circuitBreaker =
                new LocalCircuitBreaker(failRateForClose, idleTimeForOpen, passRateForHalfOpen, failNumForHalfOpen);
            CIRCUITBREAKER_CACHE.put(uri, circuitBreaker);
        }
        servletRequest.setAttribute("_CircuitBreaker", circuitBreaker);
        if (circuitBreaker.canPassCheck()) {
            return null;
        } else {
            // 被熔断了，返回定义Mock数据出去
            HttpResponse response = PluginUtil.createResponse(HttpResponseStatus.OK, servletRequest.getNettyRequest(),
                definition.getFallback());
            HttpUtil.setKeepAlive(response, false);
            return response;
        }
    }

}

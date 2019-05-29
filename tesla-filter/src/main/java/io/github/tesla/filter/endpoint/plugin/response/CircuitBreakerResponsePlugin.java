package io.github.tesla.filter.endpoint.plugin.response;

import io.github.tesla.filter.AbstractResponsePlugin;
import io.github.tesla.filter.endpoint.definition.CircuitBreakerDefinition;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.support.circuitbreaker.CircuitBreaker;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.netty.handler.codec.http.HttpResponse;

@EndpointRequestPlugin(filterType = "CircuitBreakerRequestPlugin", definitionClazz = CircuitBreakerDefinition.class,
    filterOrder = 13, filterName = "熔断插件")
public class CircuitBreakerResponsePlugin extends AbstractResponsePlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse,
        Object filterParam) {
        CircuitBreaker circuitBreaker = (CircuitBreaker)servletRequest.getAttribute("_CircuitBreaker");
        if (httpResponse.status().code() >= 300 && circuitBreaker != null) {
            circuitBreaker.countFailNum();
        }
        servletRequest.removeAttribute("_CircuitBreaker");
        return httpResponse;
    }

}

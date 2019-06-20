package io.github.tesla.filter.endpoint.plugin.response;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.tesla.filter.AbstractResponsePlugin;
import io.github.tesla.filter.endpoint.definition.CircuitBreakerDefinition;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.netty.handler.codec.http.HttpResponse;

@EndpointRequestPlugin(filterType = "CircuitBreakerRequestPlugin", definitionClazz = CircuitBreakerDefinition.class,
    filterOrder = 13, filterName = "熔断插件")
public class CircuitBreakerResponsePlugin extends AbstractResponsePlugin {

    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerResponsePlugin.class);

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse,
        Object filterParam) {
        String uri = servletRequest.getRequestURI();
        Optional<CircuitBreaker> cboptional = CircuitBreakerRegistry.ofDefaults().find(uri);
        long start = (Long)servletRequest.getAttribute("_CircuitBreakerStart");
        long durationInNanos = System.nanoTime() - start;
        if (httpResponse.status().code() >= 300 && cboptional.isPresent()) {
            logger.error("backend service is error,the status code is: {} ", httpResponse.status().code());
            cboptional.get().onError(durationInNanos, new RuntimeException(httpResponse.status().reasonPhrase()));
        } else {
            cboptional.get().onSuccess(durationInNanos);
        }
        servletRequest.removeAttribute("_CircuitBreakerStart");
        return httpResponse;
    }

}

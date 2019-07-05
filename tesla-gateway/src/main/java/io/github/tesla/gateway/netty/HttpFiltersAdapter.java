/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.github.tesla.gateway.netty;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import io.github.tesla.common.service.SpringContextHolder;
import io.github.tesla.filter.endpoint.plugin.request.RpcRoutingRequestPlugin;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.support.enums.RouteTypeEnum;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.github.tesla.filter.utils.ProxyUtils;
import io.github.tesla.gateway.cache.FilterCache;
import io.github.tesla.gateway.excutor.ServiceRouterExecutor;
import io.github.tesla.gateway.metrics.MetricsExporter;
import io.github.tesla.gateway.netty.filter.HttpRequestFilterChain;
import io.github.tesla.gateway.netty.filter.HttpResponseFilterChain;
import io.github.tesla.gateway.netty.router.DirectRouting;
import io.github.tesla.gateway.netty.router.DubboRouting;
import io.github.tesla.gateway.netty.router.GrpcRouting;
import io.github.tesla.gateway.netty.router.SpringCloudRouting;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;

public class HttpFiltersAdapter {
    private static Logger logger = LoggerFactory.getLogger(HttpFiltersAdapter.class);

    private final ChannelHandlerContext ctx;

    private final NettyHttpServletRequest serveletRequest;

    private long forwardTime = 0L;

    private long receivedTime = 0L;

    public HttpFiltersAdapter(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.serveletRequest = new NettyHttpServletRequest((FullHttpRequest)originalRequest, ctx);
    }

    private void logEnd(HttpResponse response) {
        if (!SpringContextHolder.isEnabledMetrcis())
            return;
        long forwardTime = System.currentTimeMillis() - (this.forwardTime == 0L ? this.receivedTime : this.forwardTime);
        long completeTime = System.currentTimeMillis() - this.receivedTime;
        logger.info(serveletRequest.getRequestURI() + " gateway, forward took [" + forwardTime + " ms], complete took ["
            + completeTime + "]");
        try {
            FullHttpResponse fullHttpResponse = (FullHttpResponse)response;
            logger.info("gateway return request! method:{},url:{},status:{},param:{}", serveletRequest.getMethod(),
                serveletRequest.getRequestURI(), response.status().code(),
                PluginUtil.convertByteBufToString(fullHttpResponse.content().copy()));
            MetricsExporter.returnedSize(serveletRequest.getMethod(), serveletRequest.getRequestURI(),
                fullHttpResponse.status().code(), fullHttpResponse.content().readableBytes());
            MetricsExporter.returned(serveletRequest.getMethod(), serveletRequest.getRequestURI(),
                fullHttpResponse.status().code(), serveletRequest);
        } catch (Throwable e) {
            logger.warn("log metrics failed", e);
        }
    }

    private void logForward(HttpObject httpObject) {
        if (!SpringContextHolder.isEnabledMetrcis())
            return;
        this.forwardTime = System.currentTimeMillis();
        FullHttpRequest fullHttpRequest = (FullHttpRequest)httpObject;
        try {
            logger.info("gateway forward request! method:{},url:{},host:{}", fullHttpRequest.method().name(),
                fullHttpRequest.uri(), fullHttpRequest.headers().get(HttpHeaderNames.HOST));
            MetricsExporter.forward(serveletRequest.getMethod(), serveletRequest.getRequestURI(),
                PluginUtil.convertByteBufToString(fullHttpRequest.content().copy()), serveletRequest);
        } catch (Throwable e) {
            logger.warn("log metrics failed", e);
        }
    }

    private void logStart() {
        if (!SpringContextHolder.isEnabledMetrcis())
            return;
        this.receivedTime = System.currentTimeMillis();
        try {
            String requestParam = JsonUtils.serializeToJson(serveletRequest.getParameterMap());
            logger.info("gateway received request! method:{},url:{},param:{}", serveletRequest.getMethod(),
                serveletRequest.getRequestURI(), requestParam);
            MetricsExporter.receive(serveletRequest.getMethod(), serveletRequest.getRequestURI(), requestParam,
                serveletRequest);
            MetricsExporter.receiveSize(serveletRequest.getMethod(), serveletRequest.getRequestURI(),
                serveletRequest.getNettyRequest().content().readableBytes());
        } catch (Throwable e) {
            logger.warn("log metrics failed");
        }
    }

    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
        logStart();
        HttpResponse httpResponse = null;
        try {
            httpResponse = HttpRequestFilterChain.doFilter(serveletRequest, httpObject, ctx);
            if (httpResponse != null) {
                return httpResponse;
            }
        } catch (Throwable e) {
            httpResponse = ProxyUtils.createFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_GATEWAY);
            HttpUtil.setKeepAlive(httpResponse, false);
            logger.error("Client connectTo proxy request failed", e);
            return httpResponse;
        }
        // 路由
        ServiceRouterExecutor routerCache = SpringContextHolder.getBean(FilterCache.class)
            .loadServiceCache(serveletRequest.getRequestURI()).getRouterCache();
        if (RouteTypeEnum.DUBBO.getCode().equalsIgnoreCase(routerCache.getRouteType())) {
            String rpcParamJson = serveletRequest.getStringAttribute(RpcRoutingRequestPlugin.RPC_PARAM_JSON);
            httpResponse = DubboRouting.callRemote(serveletRequest, httpObject, rpcParamJson);
            serveletRequest.removeAttribute(RpcRoutingRequestPlugin.RPC_PARAM_JSON);
        } else if (RouteTypeEnum.GRPC.getCode().equalsIgnoreCase(routerCache.getRouteType())) {
            String rpcParamJson = serveletRequest.getStringAttribute(RpcRoutingRequestPlugin.RPC_PARAM_JSON);
            httpResponse = GrpcRouting.callRemote(serveletRequest, httpObject, rpcParamJson);
            serveletRequest.removeAttribute(RpcRoutingRequestPlugin.RPC_PARAM_JSON);
        } else if (RouteTypeEnum.SpringCloud.getCode().equalsIgnoreCase(routerCache.getRouteType())) {
            httpResponse = SpringCloudRouting.callRemote(serveletRequest, httpObject, routerCache.getParamJson());
        } else if (RouteTypeEnum.DirectRoute.getCode().equalsIgnoreCase(routerCache.getRouteType())) {
            httpResponse = DirectRouting.callRemote(serveletRequest, httpObject, routerCache.getParamJson());
        }
        return httpResponse;
    }

    public HttpObject proxyToClientResponse(HttpObject httpObject) {
        if (httpObject instanceof HttpResponse) {
            HttpResponse serverResponse = (HttpResponse)httpObject;
            HttpResponse response = HttpResponseFilterChain.doFilter(serveletRequest, serverResponse, ctx);
            logEnd(serverResponse);
            return response;
        } else {
            return httpObject;
        }
    }

    public void proxyToServerConnectionFailed() {}

    public void proxyToServerConnectionQueued() {}

    public void proxyToServerConnectionSSLHandshakeStarted() {}

    public void proxyToServerConnectionStarted() {}

    public void proxyToServerConnectionSucceeded(ChannelHandlerContext serverCtx) {}

    public HttpResponse proxyToServerRequest(HttpObject httpObject) {
        logForward(httpObject);
        return null;
    }

    public void proxyToServerResolutionFailed(String hostAndPort) {}

    public InetSocketAddress proxyToServerResolutionStarted(String resolvingServerHostAndPort) {
        return null;
    }

    public void proxyToServerResolutionSucceeded(String serverHostAndPort, InetSocketAddress resolvedRemoteAddress) {
        if (resolvedRemoteAddress == null) {
            ctx.writeAndFlush(ProxyUtils.createFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_GATEWAY));
        }
    }

    public HttpObject serverToProxyResponse(HttpObject httpObject) {
        return httpObject;
    }

    public void serverToProxyResponseReceived() {}

    public void serverToProxyResponseReceiving() {}

    public void serverToProxyResponseTimedOut() {}

    /**
     * help method
     **/
    public List<Pair<String, HttpRequest>> splitRequest(String serverHostAndPort, HttpRequest httpRequest) {
        List<Pair<String, HttpRequest>> pairs = Lists.newArrayList();
        if (serveletRequest.getAttribute(PluginDefinition.CONVERGE_ATTR_KEY) != null
            && serveletRequest.getAttribute(PluginDefinition.CONVERGE_ATTR_KEY) instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> splitRequestInfos =
                (List<Map<String, String>>)serveletRequest.getAttribute(PluginDefinition.CONVERGE_ATTR_KEY);
            for (Map<String, String> requestInfo : splitRequestInfos) {
                HttpRequest splitRequest = PluginUtil.copy(httpRequest);
                splitRequest.setUri(requestInfo.get(PluginDefinition.ROUTER_PATH));
                splitRequest.headers().set(HttpHeaderNames.HOST,
                    ProxyUtils.parseHostAndPort(requestInfo.get(PluginDefinition.HOST_AND_PORT)));
                splitRequest.headers().set(PluginDefinition.X_TESLA_CONVERGE_TAG,
                    requestInfo.get(PluginDefinition.CONVERGE_TAG));
                if (YesOrNoEnum.YES.getCode().equals(requestInfo.get(PluginDefinition.X_TESLA_ENABLE_SSL))) {
                    splitRequest.headers().add(PluginDefinition.X_TESLA_ENABLE_SSL,
                        requestInfo.get(PluginDefinition.X_TESLA_ENABLE_SSL));
                    if (StringUtils.isNotBlank(requestInfo.get(PluginDefinition.X_TESLA_SELF_SIGN_CRT))) {
                        splitRequest.headers().add(PluginDefinition.X_TESLA_SELF_SIGN_CRT,
                            requestInfo.get(PluginDefinition.X_TESLA_SELF_SIGN_CRT));
                    }
                }
                splitRequest.headers().remove(PluginDefinition.X_TESLA_ENABLE_SSL);
                splitRequest.headers().remove(PluginDefinition.X_TESLA_SELF_SIGN_CRT);
                pairs.add(new ImmutablePair<String, HttpRequest>(splitRequest.headers().get(HttpHeaderNames.HOST),
                    splitRequest));
            }
            return pairs;
        } else {
            pairs.add(new ImmutablePair<String, HttpRequest>(serverHostAndPort, httpRequest));
        }
        return pairs;
    }

    public HttpObject mergeResponse(List<Pair<String, String>> httpResponses) {
        try {
            for (Pair<String, String> responseBody : httpResponses) {
                if (StringUtils.isNotBlank(responseBody.getRight()) && !JsonUtils.isJson(responseBody.getRight())) {
                    return ProxyUtils.createFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.INTERNAL_SERVER_ERROR,
                        String.format("responseBody:%s is not json ", responseBody));
                }
            }
            HttpResponse httpResponse = ProxyUtils.createJsonFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, JsonUtils.convergeJson(httpResponses));
            return httpResponse;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            HttpResponse httpResponse = ProxyUtils.createFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return httpResponse;
        }
    }

}

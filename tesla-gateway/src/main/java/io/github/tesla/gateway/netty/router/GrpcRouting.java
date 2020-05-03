package io.github.tesla.gateway.netty.router;

import io.github.tesla.common.service.SpringContextHolder;
import io.github.tesla.filter.endpoint.definition.GRpcRoutingDefinition;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.github.tesla.filter.utils.ProxyUtils;
import io.github.tesla.gateway.cache.FilterCache;
import io.github.tesla.gateway.protocol.grpc.DynamicGrpcClient;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;

public class GrpcRouting {

    public static HttpResponse callRemote(NettyHttpServletRequest servletRequest, HttpObject httpObject,
        String routerParamJson) {
        FilterCache cacheComponent = SpringContextHolder.getBean(FilterCache.class);
        GRpcRoutingDefinition definition = JsonUtils.json2Definition(routerParamJson, GRpcRoutingDefinition.class);
        if (definition != null && cacheComponent.loadFileBytes(definition.getProtoFileId()) != null) {
            String jsonOutput =
                SpringContextHolder.getBean(DynamicGrpcClient.class).doRpcRemoteCall(definition, servletRequest);
            HttpResponse response =
                ProxyUtils.createJsonFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, jsonOutput);
            HttpUtil.setKeepAlive(response, false);
            return response;
        } else {
            return PluginUtil.createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, servletRequest.getNettyRequest(),
                "grpc routing error");
        }
    }
}

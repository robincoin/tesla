package io.github.tesla.gateway.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class HttpFiltersSourceAdapter {

    public HttpFiltersAdapter filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        return new HttpFiltersAdapter(originalRequest, ctx);
    }

    public int getMaximumRequestBufferSizeInBytes() {
        return 10 * 1024 * 1024;
    }

    public int getMaximumResponseBufferSizeInBytes() {
        return 10 * 1024 * 1024;
    }

}

package io.github.tesla.gateway.netty.transmit.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.http.LastHttpContent;

public class ProxyToServerTimeoutHandler
    extends CombinedChannelDuplexHandler<ChannelInboundHandlerAdapter, ChannelOutboundHandlerAdapter> {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyToServerTimeoutHandler.class);

    public ProxyToServerTimeoutHandler(ProxyToServerConnection proxyToServerConnection) {
        super(new InboundHandler(proxyToServerConnection), new OutboundHandler(proxyToServerConnection));
    }

    private static class InboundHandler extends ChannelInboundHandlerAdapter {

        private final ProxyToServerConnection proxyToServerConnection;

        public InboundHandler(ProxyToServerConnection proxyToServerConnection) {
            this.proxyToServerConnection = proxyToServerConnection;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            try {
                if (msg instanceof LastHttpContent) {
                    LOG.debug("[{}] Removing read timeout handler", ctx.channel().id());
                    proxyToServerConnection.removeReadTimeoutHandler();
                }
            } finally {
                super.channelRead(ctx, msg);
            }
        }
    }

    private static class OutboundHandler extends ChannelOutboundHandlerAdapter {

        private final ProxyToServerConnection proxyToServerConnection;

        public OutboundHandler(ProxyToServerConnection proxyToServerConnection) {
            this.proxyToServerConnection = proxyToServerConnection;
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            try {
                final Integer timeout = proxyToServerConnection.proxyServer.getReadTimeout();
                if (timeout != null && msg instanceof LastHttpContent) {
                    promise.addListener(e -> {
                        LOG.debug("[{}] Adding read timeout handler: {}", ctx.channel().id(), timeout);
                        proxyToServerConnection.startReadTimeoutHandler(timeout);
                    });
                }
            } finally {
                super.write(ctx, msg, promise);
            }
        }
    }
}

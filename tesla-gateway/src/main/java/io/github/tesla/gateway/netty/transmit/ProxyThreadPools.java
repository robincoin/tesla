package io.github.tesla.gateway.netty.transmit;

import java.nio.channels.spi.SelectorProvider;
import java.util.List;

import com.google.common.collect.ImmutableList;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class ProxyThreadPools {

    private final NioEventLoopGroup clientToProxyAcceptorPool;

    private final NioEventLoopGroup clientToProxyWorkerPool;

    private final NioEventLoopGroup proxyToServerWorkerPool;

    public ProxyThreadPools(SelectorProvider selectorProvider, int incomingAcceptorThreads, int incomingWorkerThreads,
        int outgoingWorkerThreads, String serverGroupName, int serverGroupId) {
        clientToProxyAcceptorPool = new NioEventLoopGroup(incomingAcceptorThreads,
            new CategorizedThreadFactory(serverGroupName, "ClientToProxyAcceptor", serverGroupId), selectorProvider);
        NioEventLoopGroup eventLoogGroup = new NioEventLoopGroup(incomingWorkerThreads + outgoingWorkerThreads,
            new CategorizedThreadFactory(serverGroupName, "ClientToProxyWorkerAndProxyToServerWorker", serverGroupId),
            selectorProvider);
        eventLoogGroup.setIoRatio(90);
        clientToProxyWorkerPool = eventLoogGroup;
        proxyToServerWorkerPool = eventLoogGroup;
    }

    public List<EventLoopGroup> getAllEventLoops() {
        return ImmutableList.<EventLoopGroup>of(clientToProxyAcceptorPool, clientToProxyWorkerPool,
            proxyToServerWorkerPool);
    }

    public NioEventLoopGroup getClientToProxyAcceptorPool() {
        return clientToProxyAcceptorPool;
    }

    public NioEventLoopGroup getClientToProxyWorkerPool() {
        return clientToProxyWorkerPool;
    }

    public NioEventLoopGroup getProxyToServerWorkerPool() {
        return proxyToServerWorkerPool;
    }
}

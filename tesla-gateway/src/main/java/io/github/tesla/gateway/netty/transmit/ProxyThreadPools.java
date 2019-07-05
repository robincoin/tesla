package io.github.tesla.gateway.netty.transmit;

import java.nio.channels.spi.SelectorProvider;
import java.util.List;

import com.google.common.collect.ImmutableList;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class ProxyThreadPools {

    private final NioEventLoopGroup clientToProxyAcceptorPool;

    private final NioEventLoopGroup clientToProxyWorkerPoolAndProxyServerWorker;

    public ProxyThreadPools(SelectorProvider selectorProvider, int incomingAcceptorThreads, int incomingWorkerThreads,
        String serverGroupName, int serverGroupId) {
        clientToProxyAcceptorPool = new NioEventLoopGroup(incomingAcceptorThreads,
            new CategorizedThreadFactory(serverGroupName, "ClientToProxyAcceptor", serverGroupId), selectorProvider);
        NioEventLoopGroup eventLoogGroup = new NioEventLoopGroup(incomingWorkerThreads,
            new CategorizedThreadFactory(serverGroupName, "ClientToProxyWorkerAndProxyToServerWorker", serverGroupId),
            selectorProvider);
        eventLoogGroup.setIoRatio(90);
        clientToProxyWorkerPoolAndProxyServerWorker = eventLoogGroup;
    }

    public List<EventLoopGroup> getAllEventLoops() {
        return ImmutableList.<EventLoopGroup>of(clientToProxyAcceptorPool, clientToProxyWorkerPoolAndProxyServerWorker);
    }

    public NioEventLoopGroup getClientToProxyAcceptorPool() {
        return clientToProxyAcceptorPool;
    }

    public NioEventLoopGroup getClientToProxyWorkerAndProxyToServerPool() {
        return clientToProxyWorkerPoolAndProxyServerWorker;
    }

}

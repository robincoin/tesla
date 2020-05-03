package io.github.tesla.gateway.netty.transmit;

import io.github.tesla.gateway.netty.transmit.support.ServerGroup;

/**
 * Configuration object for the proxy's thread pools. Controls the number of acceptor and worker threads in the Netty
 * {@link io.netty.channel.EventLoopGroup} used by the proxy.
 */
public class ThreadPoolConfiguration {
    private int acceptorThreads = ServerGroup.DEFAULT_INCOMING_ACCEPTOR_THREADS;
    private int clientToProxyAndProxyToServerWorkerThreads = ServerGroup.DEFAULT_INCOMING_OUTING_WORKER_THREADS;

    public int getAcceptorThreads() {
        return acceptorThreads;
    }

    public int getClientToProxyAndProxyToServerWorkerThreads() {
        return clientToProxyAndProxyToServerWorkerThreads;
    }

    /**
     * Set the number of acceptor threads to create. Acceptor threads accept HTTP connections from the client and queue
     * them for processing by client-to-proxy worker threads. The default value is
     * {@link ServerGroup#DEFAULT_INCOMING_ACCEPTOR_THREADS}.
     *
     * @param acceptorThreads
     *            number of acceptor threads to create
     * @return this thread pool configuration instance, for chaining
     */
    public ThreadPoolConfiguration withAcceptorThreads(int acceptorThreads) {
        this.acceptorThreads = acceptorThreads;
        return this;
    }

    /**
     * Set the number of client-to-proxy worker threads to create. Worker threads perform the actual processing of
     * client requests. The default value is {@link ServerGroup#DEFAULT_INCOMING_OUTING_WORKER_THREADS}.
     *
     * @param clientToProxyWorkerThreads
     *            number of client-to-proxy worker threads to create
     * @return this thread pool configuration instance, for chaining
     */
    public ThreadPoolConfiguration withClientToProxyAndProxyToServerWorkerThreads(int clientToProxyWorkerThreads) {
        this.clientToProxyAndProxyToServerWorkerThreads = clientToProxyWorkerThreads;
        return this;
    }

}

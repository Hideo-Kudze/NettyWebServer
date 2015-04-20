package com.HideoKuzeGits.httpserver.statistic.handlers;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Monitor the number of current connections with server.
 */

//Single instance per server.
@ChannelHandler.Sharable
public class ConnectionCountHandler extends ChannelDuplexHandler {

    public static AtomicInteger connectionsCount = new AtomicInteger(0);

    public ConnectionCountHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        connectionsCount.incrementAndGet();
        super.channelActive(ctx);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        connectionsCount.decrementAndGet();
        super.channelInactive(ctx);
    }

    /**
     *  Return current connections count.
     * @return current connections count.
     */
    public int getConnectionsCount() {
        return connectionsCount.get();
    }
}

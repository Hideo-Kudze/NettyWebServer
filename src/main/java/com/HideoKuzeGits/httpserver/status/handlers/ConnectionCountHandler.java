package com.HideoKuzeGits.httpserver.status.handlers;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicInteger;

@ChannelHandler.Sharable
public class ConnectionCountHandler extends ChannelDuplexHandler {

    public static AtomicInteger connectionsCount = new AtomicInteger(0);
    private static ConnectionCountHandler connectionCountHandler;

    private ConnectionCountHandler() {
    }

    public synchronized static ConnectionCountHandler getInstance() {

        if (connectionCountHandler == null)
            connectionCountHandler = new ConnectionCountHandler();

        return connectionCountHandler;
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

    public static int getConnectionsCount() {
        return connectionsCount.get();
    }
}

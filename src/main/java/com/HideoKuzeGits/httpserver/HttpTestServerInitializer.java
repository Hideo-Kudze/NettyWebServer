package com.HideoKuzeGits.httpserver;

import com.HideoKuzeGits.httpserver.status.handlers.ConnectionCountHandler;
import com.HideoKuzeGits.httpserver.status.handlers.TrafficStatisticHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;


public class HttpTestServerInitializer extends ChannelInitializer<SocketChannel> {




    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline p = ch.pipeline();


        TrafficStatisticHandler trafficStatisticHandler = new TrafficStatisticHandler();

        p.addLast(trafficStatisticHandler);
        p.addLast(ConnectionCountHandler.getInstance());
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpResponseEncoder());
        p.addLast(trafficStatisticHandler.getUrlHandler());
        p.addLast(trafficStatisticHandler.getRedirectHandler());
        p.addLast(new HttpTestServerHandler());

    }
}

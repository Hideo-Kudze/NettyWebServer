package com.HideoKuzeGits.httpserver;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Return "hello" page.
 */
@ChannelHandler.Sharable
public class HelloWorldHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {


        String path;
        if (msg instanceof HttpRequest) {

            String url = ((HttpRequest) msg).getUri();
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(url);
            path = queryStringDecoder.path();
            if (path.equals("/hello")) {
                ReferenceCountUtil.release(msg);

                final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
                response.headers().add("content-type", "text/html; charset=UTF-8");

                byte[] bodyBites = "Hello World".getBytes(Charset.forName("UTF-8"));
                response.content().writeBytes(bodyBites);

                //Response asynchronously.
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {

                        if (HttpHeaders.isKeepAlive((HttpRequest)msg)) {
                            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
                            ctx.writeAndFlush(response);
                        }
                        else {
                            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                        }
                    }
                }, 10_000);

            } else
                //If it is not "hello" page delegate processing to the next handlers.
                super.channelRead(ctx, msg);
        } else
            super.channelRead(ctx, msg);

    }
}


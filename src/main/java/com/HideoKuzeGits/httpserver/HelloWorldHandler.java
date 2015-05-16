package com.HideoKuzeGits.httpserver;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Return "hello" page.
 */
public class HelloWorldHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {


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

                        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                        ctx.flush();
                    }
                }, 5000);

            } else
                //If it is not "hello" page delegate processing to the next handlers.
                super.channelRead(ctx, msg);
        } else
            super.channelRead(ctx, msg);

    }
}


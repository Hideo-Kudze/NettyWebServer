package com.HideoKuzeGits.httpserver;

import com.HideoKuzeGits.httpserver.mapping.HandlerMapping;
import com.HideoKuzeGits.httpserver.controllers.HttpRequestHandler;
import com.HideoKuzeGits.httpserver.mapping.UrlHandlerMapping;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.nio.charset.Charset;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpTestServerHandler extends SimpleChannelInboundHandler<Object> {


    private HandlerMapping mapping = new UrlHandlerMapping();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {


        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);

        response.headers().add("content-type", "text/html; charset=UTF-8");
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            String url = ((HttpRequest) msg).getUri();
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(url);
            String path = queryStringDecoder.path();
            HttpRequestHandler handler = mapping.getHandler(path);
            String responseBody;

            if (handler != null) {

                try {
                    responseBody = handler.processRequest(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                    responseBody = e.toString();
                    response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                response.setStatus(HttpResponseStatus.NOT_FOUND);
                responseBody = "Page not found";
            }

            if (responseBody != null)
                response.content().writeBytes(responseBody.getBytes(Charset.forName("UTF-8")));

            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
         }


        if (msg instanceof LastHttpContent) {

            ctx.flush();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
    }


}

package com.HideoKuzeGits.httpserver;

import com.HideoKuzeGits.httpserver.controllers.HttpRequestHandler;
import com.HideoKuzeGits.httpserver.mapping.HandlerMapping;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.nio.charset.Charset;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * Delegate http Request processing to specific controller.
 */

//Single instance per server.
@ChannelHandler.Sharable
public class HttpTestServerHandler extends SimpleChannelInboundHandler<Object> {

    /**
     * The class define a mapping between requests and handler objects.
     */
    private HandlerMapping mapping;


    /**
     *
     * Process http request.
     *
     * @param msg {@link HttpRequest} or {@link LastHttpContent} instance that was received from {@link HttpRequestDecoder}.
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().add("content-type", "text/html; charset=UTF-8");

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            String url = ((HttpRequest) msg).getUri();
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(url);
            String path = queryStringDecoder.path();
            //Find handler for this url.
            HttpRequestHandler handler = mapping.getHandler(path);
            String responseBody;

            if (handler != null) {

                try {
                    //Delegate the processing of the request to handler.
                    responseBody = handler.processRequest(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                    //Show exception on page.
                    responseBody = e.toString();
                    response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                //If there is no handler for this page return "Page not found".
                response.setStatus(HttpResponseStatus.NOT_FOUND);
                responseBody = "Page not found";
            }

            if (responseBody != null) {
                byte[] bodyBites = responseBody.getBytes(Charset.forName("UTF-8"));
                response.content().writeBytes(bodyBites);
            }

            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
         }

        //If request ended flush context
        if (msg instanceof LastHttpContent) {
            ctx.flush();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
    }

    public void setMapping(HandlerMapping mapping) {
        this.mapping = mapping;
    }
}

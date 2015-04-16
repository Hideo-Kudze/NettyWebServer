package com.HideoKuzeGits.httpserver;

import com.HideoKuzeGits.httpserver.logs.ConnectionLog;
import com.HideoKuzeGits.httpserver.logs.ConnectionLogDao;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.net.InetSocketAddress;

public class TrafficStatisticHandler extends ChannelDuplexHandler {


    private Integer readBytes = 0;
    private Integer writtenBytes = 0;
    private Long startTime;
    private Long lastReadTime;
    private Long lastWriteTime;
    private String url;
    private String ip;
    private String redirectUri;


    private ChannelInboundHandlerAdapter urlHandler = new ChannelInboundHandlerAdapter() {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof DefaultHttpRequest){
                url = ((DefaultHttpRequest) msg).getUri();
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(url);
                url = queryStringDecoder.path();
            }
            super.channelRead(ctx, msg);
        }
    };


    private ChannelOutboundHandler redirectHandler = new ChannelOutboundHandlerAdapter() {

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

            if (msg instanceof DefaultFullHttpResponse) {
                HttpResponse msg1 = (DefaultFullHttpResponse) msg;
                if (msg1.getStatus().equals(HttpResponseStatus.MOVED_PERMANENTLY))
                    redirectUri = ((DefaultFullHttpResponse) msg).headers().get("location");

            super.write(ctx, msg, promise);
            }
        }
    };

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        startTime = System.nanoTime();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        ip = inetSocketAddress.getAddress().getHostAddress();
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf byteBuf = (ByteBuf) msg;
        readBytes += byteBuf.readableBytes();
        lastReadTime = System.nanoTime();
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        channelWritabilityChanged(ctx);

        ByteBuf byteBuf = (ByteBuf) msg;
        writtenBytes += byteBuf.writableBytes();

        ctx.write(msg, promise).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                lastWriteTime = System.nanoTime();
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        ConnectionLog connectionLog = createConnectionLog();
        new ConnectionLogDao().write(connectionLog);
        super.channelInactive(ctx);
    }

    public ChannelHandler getUrlHandler() {
        return urlHandler;
    }

    public ConnectionLog createConnectionLog() {


        Float downloadSpeed;
        if (lastReadTime != null)
            downloadSpeed = (((float) readBytes) / (lastReadTime - startTime)) * 1e9f;
        else
            downloadSpeed = 0f;

        Float uploadSpeed;
        if (lastWriteTime != null)
            uploadSpeed = ((float) writtenBytes) / (lastWriteTime - startTime) * 1e9f;
        else
            uploadSpeed = 0f;


        ConnectionLog connectionLog = new ConnectionLog(ip, url, startTime, readBytes, writtenBytes);
        connectionLog.setRedirectUrl(redirectUri);
        connectionLog.setDownloadSpeed(downloadSpeed);
        connectionLog.setUploadSpeed(uploadSpeed);
        return connectionLog;
    }

    public ChannelOutboundHandler getRedirectHandler() {
        return redirectHandler;
    }
}

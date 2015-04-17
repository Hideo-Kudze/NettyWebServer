package com.HideoKuzeGits.httpserver.status.handlers;

import com.HideoKuzeGits.httpserver.status.logs.ConnectionLog;
import com.HideoKuzeGits.httpserver.status.logs.ConnectionLogSaver;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.net.InetSocketAddress;

public class TrafficStatisticHandler extends ChannelDuplexHandler {


    private Integer readBytes = 0;
    private Integer writtenBytes = 0;
    private Long startTimeNanos;
    private Long startTimeMillis;
    private Long lastReadTime;
    private Long writeDuration = 0l;
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

        startTimeNanos = System.nanoTime();
        startTimeMillis = System.currentTimeMillis();
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

        final Long writeStartTime = System.nanoTime();

        channelWritabilityChanged(ctx);

        final ByteBuf byteBuf = (ByteBuf) msg;
        writtenBytes += byteBuf.writerIndex();

        ctx.write(msg, promise).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                writeDuration += System.nanoTime() - writeStartTime;
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        ConnectionLog connectionLog = createConnectionLog();
        ConnectionLogSaver.getInstance().save(connectionLog);
        super.channelInactive(ctx);
    }

    public ChannelHandler getUrlHandler() {
        return urlHandler;
    }

    public ConnectionLog createConnectionLog() {


        Double downloadSpeed;
        if (lastReadTime != null)
            downloadSpeed = (((double)readBytes) / (lastReadTime - startTimeNanos)) * 1e9;
        else
            downloadSpeed = 0d;

        Double uploadSpeed;
        if (writeDuration != 0)
            uploadSpeed = (((double)writtenBytes) / writeDuration) * 1e9;
        else
            uploadSpeed = 0d;


        ConnectionLog connectionLog = new ConnectionLog(ip, url, startTimeMillis, readBytes, writtenBytes);
        connectionLog.setRedirectUrl(redirectUri);
        connectionLog.setDownloadSpeed(downloadSpeed);
        connectionLog.setUploadSpeed(uploadSpeed);
        return connectionLog;
    }

    public ChannelOutboundHandler getRedirectHandler() {
        return redirectHandler;
    }
}

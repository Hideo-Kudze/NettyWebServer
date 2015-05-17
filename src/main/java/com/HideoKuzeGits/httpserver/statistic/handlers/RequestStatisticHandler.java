package com.HideoKuzeGits.httpserver.statistic.handlers;

import com.HideoKuzeGits.httpserver.statistic.ServerStatistic;
import com.HideoKuzeGits.httpserver.statistic.logs.ConnectionLog;
import com.HideoKuzeGits.httpserver.statistic.logs.ConnectionLogSaver;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.net.InetSocketAddress;


/**
 * Gather statistic about request.
 */
//Instance per request.
public class RequestStatisticHandler extends ChannelDuplexHandler {


    private String url;
    private String ip;
    private String redirectUri;

    /**
     * Start time of the request,
     */
    private Long startTimeNanoseconds;
    private Long startTimeMiliseconds;
    private Integer readBytes = 0;
    private Long lastWriteCompleteTime = 0l;
    private Integer writtenBytes = 0;

    private ConnectionLogSaver connectionLogSaver;

    /**
     * ServerStatistic to merge it with new logs.
     */
    private ServerStatistic serverStatistic;

    /**
     * Get request url.
     */
    private ChannelInboundHandlerAdapter urlHandler = new ChannelInboundHandlerAdapter() {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof DefaultHttpRequest) {
                url = ((DefaultHttpRequest) msg).getUri();
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(url);
                url = queryStringDecoder.path();
            }
            super.channelRead(ctx, msg);
        }
    };

    /**
     * Get redirect url if response contains it.
     */
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

    private boolean connectionLogsSaved = false;

    private ChannelOutboundHandler connectionEndedHandler = new ChannelOutboundHandlerAdapter() {


        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

            ctx.write(msg, promise).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    connectionEnded();
                }
            });
        }
    };


    /**
     * Connection established.
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        setIpAndStartTime(ctx);
        super.channelActive(ctx);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        if (connectionLogsSaved)
            setIpAndStartTime(ctx);

        connectionLogsSaved = false;
        ByteBuf byteBuf = (ByteBuf) msg;
        readBytes += byteBuf.readableBytes() + byteBuf.readerIndex();
        super.channelRead(ctx, msg);
    }


    private void setIpAndStartTime(ChannelHandlerContext ctx) {
        connectionLogsSaved = false;
        startTimeNanoseconds = System.nanoTime();
        startTimeMiliseconds = System.currentTimeMillis();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        ip = inetSocketAddress.getAddress().getHostAddress();
    }


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        final ByteBuf byteBuf = (ByteBuf) msg;
        writtenBytes += byteBuf.readableBytes() + byteBuf.readerIndex();

        ctx.write(msg, promise).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {

                lastWriteCompleteTime = System.nanoTime();
            }
        });
    }

    /**
     * Calls when connection ended.
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        connectionEnded();
        super.channelInactive(ctx);
    }


    /**
     * Save connection logs. Merge server statistic with new log.
     */
    private void connectionEnded() {

        if (!connectionLogsSaved) {
            ConnectionLog connectionLog = createConnectionLog();
            connectionLogSaver.save(connectionLog);
            serverStatistic.addData(connectionLog);
        }
        connectionLogsSaved = true;

        resetVariables();

    }

    private void resetVariables() {
        url = null;
        ip = null;
        redirectUri = null;
        startTimeNanoseconds = null;
        startTimeMiliseconds = null;
        readBytes = 0;
        lastWriteCompleteTime = 0l;
        writtenBytes = 0;
    }

    public ChannelHandler getUrlHandler() {
        return urlHandler;
    }

    /**
     * Process request statistic and produce connection log.
     *
     * @return log of this request.
     */
    public ConnectionLog createConnectionLog() {

        Integer speed = 0;
        Long writeDuration = lastWriteCompleteTime - startTimeNanoseconds;
        if (writeDuration != 0)
            speed = (int) (writtenBytes * 1e9 / writeDuration);

        ConnectionLog connectionLog = new ConnectionLog(ip, url, startTimeMiliseconds, readBytes, writtenBytes);
        connectionLog.setRedirectUrl(redirectUri);
        connectionLog.setSpeed(speed);
        return connectionLog;
    }

    public ChannelOutboundHandler getRedirectHandler() {
        return redirectHandler;
    }

    public ChannelOutboundHandler getConnectionEndedHandler() {
        return connectionEndedHandler;
    }

    public void setConnectionLogSaver(ConnectionLogSaver connectionLogSaver) {
        this.connectionLogSaver = connectionLogSaver;
    }

    public void setServerStatistic(ServerStatistic serverStatistic) {
        this.serverStatistic = serverStatistic;
    }
}

package com.HideoKuzeGits.httpserver;

import com.HideoKuzeGits.httpserver.controllers.ServerStatisticController;
import com.HideoKuzeGits.httpserver.controllers.StopServerController;
import com.HideoKuzeGits.httpserver.mapping.AnnotationHandlerMapping;
import com.HideoKuzeGits.httpserver.statistic.ServerStatistic;
import com.HideoKuzeGits.httpserver.statistic.ServerStatisticService;
import com.HideoKuzeGits.httpserver.statistic.handlers.ConnectionCountHandler;
import com.HideoKuzeGits.httpserver.statistic.handlers.RequestStatisticHandler;
import com.HideoKuzeGits.httpserver.statistic.logs.ConnectionLog;
import com.HideoKuzeGits.httpserver.statistic.logs.ConnectionLogLoader;
import com.HideoKuzeGits.httpserver.statistic.logs.ConnectionLogSaver;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.util.SortedSet;

/**
 * Inject dependencies. Manage their life cycle and init new channels.
 *
 * @see io.netty.channel.ChannelInitializer
 */

//Single instance per server.
public class HttpTestServerInitializer extends ChannelInitializer<SocketChannel> {


    private HttpTestServerHandler httpTestServerHandler;
    private ConnectionCountHandler connectionCountHandler;
    private ConnectionLogSaver connectionLogSaver;
    private AnnotationHandlerMapping annotationHandlerMapping;
    private ServerStatistic serverStatistic;
    private HelloWorldHandler helloWorldHandler;


    /**
     * Create and wire singletons. Calls once per server life.
     */
    public HttpTestServerInitializer() {

        httpTestServerHandler = new HttpTestServerHandler();
        connectionCountHandler = new ConnectionCountHandler();
        connectionLogSaver = new ConnectionLogSaver();
        annotationHandlerMapping = new AnnotationHandlerMapping();
        helloWorldHandler = new HelloWorldHandler();

        //Load connection logs and convert them to server statistic.
        ConnectionLogLoader connectionLogLoader = new ConnectionLogLoader();
        SortedSet<ConnectionLog> connectionLogs = connectionLogLoader.load();
        serverStatistic = new ServerStatistic(connectionLogs);

        ServerStatisticService serverStatisticService = new ServerStatisticService();
        ServerStatisticController serverStatisticController = new ServerStatisticController();
        StopServerController stopServerController = new StopServerController();


        serverStatisticService.setServerStatistic(serverStatistic);
        serverStatisticService.setConnectionCountHandler(connectionCountHandler);
        serverStatisticController.setServerStatisticService(serverStatisticService);
        stopServerController.setServerInitializer(this);

        //Controllers that need be wired creates here and than added to mappings manually.
        annotationHandlerMapping.addController(serverStatisticController);
        annotationHandlerMapping.addController(stopServerController);


        httpTestServerHandler.setMapping(annotationHandlerMapping);
    }


    /**
     *
     * Create and wire components with lifecycle of request. Calls for every request.<br/>
     * Add handlers to pipeline.
     *
     * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {


        RequestStatisticHandler requestStatisticHandler = new RequestStatisticHandler();
        requestStatisticHandler.setConnectionLogSaver(connectionLogSaver);
        requestStatisticHandler.setServerStatistic(serverStatistic);

        ChannelPipeline p = ch.pipeline();
        p.addLast(requestStatisticHandler);
        p.addLast(connectionCountHandler);
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpResponseEncoder());

        //Inbound handler that handle information about urls right after HttpResponseEncoder send it.
        p.addLast(requestStatisticHandler.getUrlHandler());

        //Outbound handler that handle information about redirect urls right after HttpTestServerHandler send it.
        p.addLast(requestStatisticHandler.getRedirectHandler());
        p.addLast(requestStatisticHandler.getConnectionEndedHandler());
        p.addLast(helloWorldHandler);
        p.addLast(httpTestServerHandler);
    }


    /**
     * Stops saving logs and updating statistic,
     */
    public void stop(){
        connectionLogSaver.interrupt();
        serverStatistic.stopMerging();
    }
}

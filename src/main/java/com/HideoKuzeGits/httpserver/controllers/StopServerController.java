package com.HideoKuzeGits.httpserver.controllers;

import com.HideoKuzeGits.httpserver.HttpTestServerInitializer;
import com.HideoKuzeGits.httpserver.Server;
import com.HideoKuzeGits.httpserver.mapping.RequestMapping;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;


/**
 * Request to this controller stops the server.
 */
@RequestMapping("/stopServer")
public class StopServerController implements HttpRequestHandler {


    private HttpTestServerInitializer serverInitializer;

    @Override
    public String processRequest(HttpRequest request, HttpResponse response) throws Exception {

        Server.stopCurrentServer();
        serverInitializer.stop();
        return "";
    }

    public void setServerInitializer(HttpTestServerInitializer serverInitializer) {
        this.serverInitializer = serverInitializer;
    }
}

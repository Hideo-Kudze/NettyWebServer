package com.HideoKuzeGits.httpserver.controllers;

import com.HideoKuzeGits.httpserver.mapping.RequestMapping;
import com.HideoKuzeGits.httpserver.statistic.ServerStatisticService;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Return server statistic page.
 */
@RequestMapping("/status")
public class ServerStatisticController implements HttpRequestHandler {

    private ServerStatisticService serverStatisticService;

    @Override
    public String processRequest(HttpRequest request, HttpResponse response) throws Exception {
        return serverStatisticService.getStatisticPage();
    }

    public void setServerStatisticService(ServerStatisticService serverStatisticService) {
        this.serverStatisticService = serverStatisticService;
    }
}

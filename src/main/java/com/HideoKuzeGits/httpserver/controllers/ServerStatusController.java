package com.HideoKuzeGits.httpserver.controllers;

import com.HideoKuzeGits.httpserver.mapping.RequestMapping;
import com.HideoKuzeGits.httpserver.status.StatusCashService;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;


@RequestMapping("/status")

public class ServerStatusController implements HttpRequestHandler {

    private StatusCashService statusCashService = new StatusCashService();

    @Override
    public String processRequest(HttpRequest request, HttpResponse response) throws Exception {

        return statusCashService.getStatusPage();
    }
}

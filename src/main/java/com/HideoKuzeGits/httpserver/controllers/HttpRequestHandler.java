package com.HideoKuzeGits.httpserver.controllers;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 *  Receive and respond to requests from Web clients, across HTTP.
 */
public interface HttpRequestHandler {

    /**
     *
     * Called to process http request.
     *
     * @param request current request.
     * @param response produced response.
     * @return body of html response.
     * @throws Exception exception during request processing.
     */
    public String processRequest(HttpRequest request, HttpResponse response) throws Exception;
}

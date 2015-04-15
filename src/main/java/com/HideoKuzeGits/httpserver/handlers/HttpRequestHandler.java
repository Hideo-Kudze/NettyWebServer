package com.HideoKuzeGits.httpserver.handlers;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public interface HttpRequestHandler {

    //Return response body.
    public String processRequest(HttpRequest request, HttpResponse response) throws Exception;
}

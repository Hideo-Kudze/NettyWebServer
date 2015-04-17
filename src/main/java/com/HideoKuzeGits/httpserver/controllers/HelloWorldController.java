package com.HideoKuzeGits.httpserver.controllers;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import com.HideoKuzeGits.httpserver.mapping.RequestMapping;

@RequestMapping("/")
public class HelloWorldController implements HttpRequestHandler{


    @Override
    public String processRequest(HttpRequest request, HttpResponse response) throws InterruptedException {

        Thread.sleep(10 * 1000);
        return "Hello World";
    }
}

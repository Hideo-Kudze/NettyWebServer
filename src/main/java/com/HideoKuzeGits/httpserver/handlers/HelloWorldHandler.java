package com.HideoKuzeGits.httpserver.handlers;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import com.HideoKuzeGits.httpserver.mapping.HttpRequestHandler;
import com.HideoKuzeGits.httpserver.mapping.RequestMapping;

@RequestMapping("/")
public class HelloWorldHandler implements HttpRequestHandler{


    @Override
    public String processRequest(HttpRequest request, HttpResponse response) {
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Hello World";
    }
}

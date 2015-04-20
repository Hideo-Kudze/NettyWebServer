package com.HideoKuzeGits.httpserver.controllers;

import com.HideoKuzeGits.httpserver.mapping.RequestMapping;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * Redirects to url in request parameters.
 */
@RequestMapping("/redirect")
public class RedirectController implements HttpRequestHandler {

    @Override
    public String processRequest(HttpRequest request, HttpResponse response) throws HttpHandlerException {

        String uri = request.getUri();
        Map<String, List<String>> parameters = new QueryStringDecoder(uri).parameters();

        String redirectUrl;
        try {
            redirectUrl = parameters.get("url").get(0);
        } catch (NullPointerException e) {
            throw new HttpHandlerException("Url parameter is required.");
        } catch (IndexOutOfBoundsException e) {
            throw new HttpHandlerException("Url parameter is required.");
        }

        response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
        response.headers().add("Location", redirectUrl);
        return null;
    }
}

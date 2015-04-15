package com.HideoKuzeGits.httpserver.mapping;

public interface HandlerMapping {
    HttpRequestHandler getHandler(String path);
}

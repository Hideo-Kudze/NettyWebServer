package com.HideoKuzeGits.httpserver.mapping;

import com.HideoKuzeGits.httpserver.handlers.HttpRequestHandler;

public interface HandlerMapping {
    HttpRequestHandler getHandler(String path);
}

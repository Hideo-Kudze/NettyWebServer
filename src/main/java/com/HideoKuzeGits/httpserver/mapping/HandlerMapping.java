package com.HideoKuzeGits.httpserver.mapping;

import com.HideoKuzeGits.httpserver.controllers.HttpRequestHandler;

public interface HandlerMapping {
    HttpRequestHandler getHandler(String path);
}

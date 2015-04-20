package com.HideoKuzeGits.httpserver.mapping;

import com.HideoKuzeGits.httpserver.controllers.HttpRequestHandler;

/**
 * Interface to be implemented by objects that define a mapping between requests and handler objects.
 */
public interface HandlerMapping {

    /**
     * Return a handler for this url
     * @param url of current HTTP request.
     * @return instance of handler object or null if no mapping found.
     */
    HttpRequestHandler getHandler(String url);
}

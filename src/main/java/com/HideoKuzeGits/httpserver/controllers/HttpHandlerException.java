package com.HideoKuzeGits.httpserver.controllers;

/**
 * Defines exception a handler can throw when it encounters difficulty.
 */
public class HttpHandlerException extends Exception {

    public HttpHandlerException(String message) {
        super(message);
    }
}

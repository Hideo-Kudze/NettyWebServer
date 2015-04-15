package com.HideoKuzeGits.httpserver.mapping;


import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UrlHandlerMapping implements HandlerMapping {


    private Map<Class, HttpRequestHandler> httpRequestHandlers = new HashMap<Class, HttpRequestHandler>();

    @Override
    public HttpRequestHandler getHandler(String path) {

        ImmutableSet<ClassPath.ClassInfo> allClasses = null;

        try {
            ClassPath classPath = ClassPath.from(this.getClass().getClassLoader());
            allClasses = classPath.getTopLevelClasses("com.HideoKuzeGits.httpserver.handlers");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (ClassPath.ClassInfo classInfo : allClasses) {

            Class<?> clazz = classInfo.load();

            if (clazz.isAnnotationPresent(RequestMapping.class) || clazz.isAssignableFrom(HttpRequestHandler.class)) {
                String value = clazz.getAnnotation(RequestMapping.class).value();
                if (value.equals(path)) {
                    try {
                        HttpRequestHandler httpRequestHandler = httpRequestHandlers.get(clazz);
                        if (httpRequestHandler == null) {
                            httpRequestHandler = (HttpRequestHandler) clazz.newInstance();
                            httpRequestHandlers.put(clazz, httpRequestHandler);
                        }
                        return httpRequestHandler;
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }
}

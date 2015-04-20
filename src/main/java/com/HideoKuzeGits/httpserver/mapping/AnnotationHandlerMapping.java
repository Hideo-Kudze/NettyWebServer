package com.HideoKuzeGits.httpserver.mapping;


import com.HideoKuzeGits.httpserver.controllers.HttpRequestHandler;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the HandlerMapping interface that maps handlers based on HTTP paths expressed
 * through the {@link RequestMapping} annotation.
 */

//Single instance per server.
public class AnnotationHandlerMapping implements HandlerMapping {

    /**
     * Map URL paths to handlers that process request to this urls.
     */
    private Map<String, HttpRequestHandler> httpRequestControllers = new HashMap<String, HttpRequestHandler>();

    /**
     * Scans {@link com.HideoKuzeGits.httpserver.controllers} and fill mappings.
    */
    public AnnotationHandlerMapping() {

        ImmutableSet<ClassPath.ClassInfo> allClasses = null;

        try {
            ClassPath classPath = ClassPath.from(this.getClass().getClassLoader());
            allClasses = classPath.getTopLevelClasses("com.HideoKuzeGits.httpserver.controllers");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (ClassPath.ClassInfo classInfo : allClasses) {

            Class<?> clazz = classInfo.load();

            if (clazz.isAnnotationPresent(RequestMapping.class) && HttpRequestHandler.class.isAssignableFrom(clazz)) {
                String url = clazz.getAnnotation(RequestMapping.class).value();
                try {
                    HttpRequestHandler httpRequestHandler = (HttpRequestHandler) clazz.newInstance();
                    httpRequestControllers.put(url, httpRequestHandler);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Return handler from {@link com.HideoKuzeGits.httpserver.controllers} were {@link RequestMapping#value}
     * equals to the request url.
     *
     * @param url of current HTTP request.
     * @return instance of handler object or null if no mapping found.
     */
    @Override
    public HttpRequestHandler getHandler(String url) {

        return httpRequestControllers.get(url);
    }

    /**
     * Add controller to mapping manually.
     */
    public void addController(HttpRequestHandler controller) {

        String path = controller.getClass().getAnnotation(RequestMapping.class).value();
        httpRequestControllers.put(path, controller);
    }
}

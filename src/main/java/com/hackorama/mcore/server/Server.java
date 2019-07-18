package com.hackorama.mcore.server;


import java.util.Map;
import java.util.function.Function;

import com.hackorama.mcore.http.Method;
import com.hackorama.mcore.http.Request;
import com.hackorama.mcore.http.Response;

public interface Server {

    public String getHost();

    public String getName();

    public int getPort();

    public void setRoutes(Method method, String path, Function<Request, Response> handler);

    public void setRoutes(Map<Method, Map<String, Function<Request, Response>>> routeHandlerMap);

    public boolean start();

    public void stop();

}

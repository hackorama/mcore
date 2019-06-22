package com.hackorama.mcore.server;


import java.util.Map;
import java.util.function.Function;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;

public interface Server {

    public String getHost();

    public String getName();

    public int getPort();

    public void setRoutes(HttpMethod method, String path, Function<Request, Response> handler);

    public void setRoutes(Map<HttpMethod, Map<String, Function<Request, Response>>> routeHandlerMap);

    public boolean start();

    public void stop();

}

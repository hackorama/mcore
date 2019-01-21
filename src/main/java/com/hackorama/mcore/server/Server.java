package com.hackorama.mcore.server;


import java.util.function.Function;

import com.hackorama.mcore.common.HttpMethod;

public interface Server {

    public boolean start();

    public void stop();

    public String getName();

    public void setRoutes(HttpMethod method, String path, Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response> handler);

}

package com.hackorama.mcore.server;


import java.lang.reflect.Method;

import com.hackorama.mcore.common.HttpMethod;

public interface Server {

    public boolean start();

    public void stop();

    public String getName();

    public void setRoutes(HttpMethod method, String path, Method handler);

}

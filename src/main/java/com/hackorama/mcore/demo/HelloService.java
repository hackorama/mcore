package com.hackorama.mcore.demo;

import com.google.gson.Gson;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.service.BaseService;

public class HelloService extends BaseService {

    public static Response getHello(Request request) {
        String name = request.getParams().get("name");
        if (name != null) {
            return new Response(new Gson().toJson("hello " + name));
        } else {
            return new Response(new Gson().toJson("hello world"));
        }
    }

    @Override
    public void configure() {
        server.setRoutes(HttpMethod.GET, "/hello", HelloService::getHello);
        server.setRoutes(HttpMethod.GET, "/hello/{name}", HelloService::getHello);
    }

}

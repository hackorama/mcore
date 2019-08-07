package com.hackorama.mcore.common;

import com.hackorama.mcore.http.Request;
import com.hackorama.mcore.http.Response;
import com.hackorama.mcore.service.Service;

public class HelloService extends Service {

    public static Response getHello(Request request) {
        String name = request.getPathParams().get("name");
        if (name != null) {
            return new Response("hello " + name);
        } else {
            return new Response("hello world");
        }
    }

    @Override
    public void configure() {
        GET("/hello", HelloService::getHello);
        GET("/hello/{name}", HelloService::getHello);
    }

}

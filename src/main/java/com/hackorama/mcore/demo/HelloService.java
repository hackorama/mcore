package com.hackorama.mcore.demo;

import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.service.BaseService;

public class HelloService extends BaseService {

    public static Response getHello(Request request) {
        String name = request.getParams().get("name");
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

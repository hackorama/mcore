package com.hackorama.mcore.demo;

import com.google.gson.Gson;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.server.Server;
import com.hackorama.mcore.service.Service;

public class HelloService implements Service {

    private static Server server;
    @Override
    public Service attach(Service service) {
        return null;
    }

    private static void setServer(Server server) {
        HelloService.server = server;
    }

    @Override
    public Service configureUsing(Server server) {
        HelloService.setServer(server);
        server.setRoutes(HttpMethod.GET, "/hello", HelloService::getHello);
        server.setRoutes(HttpMethod.GET, "/hello/{name}", HelloService::getHello);
        return this;
    }

    public static Response getHello(Request request) {
        System.out.println(request.getParams());
        String name = request.getParams().get("name");
        if (name != null) {
            return new Response(new Gson().toJson("hello " + name));
        } else {
            return new Response(new Gson().toJson("hello world"));
        }
    }

    @Override
    public Service start() {
        server.start();
        return this;
    }

    @Override
    public void stop() {
        if(server != null) {
            server.stop();
        }
    }

}

package com.hackorama.mcore.server.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.server.Server;

/**
 * Spring WebFlux server implementation
 *
 * NOTE: Routes must be configured before server start, there is no way to
 * modify or remove routes after server start.
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class SpringServer implements Server {

    String name;
    int port = 8080;

    public SpringServer(String name) {
        this.name = name;
    }

    public SpringServer(String name, int port) {
        this.name = name;
        this.port = port;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setRoutes(HttpMethod method, String path,  Function<Request, Response> handler) {
        Handler.getHandlerMap().get(method).put(path, handler);
        trackParamList(path);
    }

    @Override
    public void setRoutes(
            Map<HttpMethod, Map<String, Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response>>> routeHandlerMap) {
        routeHandlerMap.forEach( (method, route) -> {
            route.forEach( (path, handler) -> {
                setRoutes(method, path, handler);
            });
        });
    }

    @Override
    public boolean start() {
        Application.start(port);
        return true;
    }

    @Override
    public void stop() {
        Application.stop();
    }

    private void trackParamList(String path) {
        List<String> params = new ArrayList<String>(Arrays.asList(path.split("/"))).stream()
                .filter(e -> e.startsWith("{") && e.endsWith("}")).collect(Collectors.toList());
        if (!params.isEmpty()) {
            Handler.getParamListMap().put(path, params);
        }
    }

}

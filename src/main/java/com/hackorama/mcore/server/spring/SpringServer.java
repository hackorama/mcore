package com.hackorama.mcore.server.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.hackorama.mcore.http.Method;
import com.hackorama.mcore.http.Request;
import com.hackorama.mcore.http.Response;
import com.hackorama.mcore.server.BaseServer;

/**
 * Spring WebFlux server implementation
 *
 * NOTE: Routes must be configured before server start, there is no way to
 * modify or remove routes after server start.
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class SpringServer extends BaseServer {

    public SpringServer(String name) {
        super(name);
    }

    public SpringServer(String name, int port) {
        super(name, port);
    }

    @Override
    public void setRoutes(Method method, String path,  Function<Request, Response> handler) {
        routeHandlerMap.get(method).put(path, handler);
        trackParamList(path);
    }

    @Override
    public boolean start() {
        Handler.setRouteHandlerMap(routeHandlerMap);
        Handler.setParamListMap(paramListMap);
        Application.start(port);
        return true;
    }

    @Override
    public void stop() {
        Application.stop();
    }

    @Override
    protected void trackParamList(String path) {
        List<String> params = new ArrayList<String>(Arrays.asList(path.split("/"))).stream()
                .filter(e -> e.startsWith("{") && e.endsWith("}")).collect(Collectors.toList());
        if (!params.isEmpty()) {
            paramListMap.put(path, params);
        }
    }

}

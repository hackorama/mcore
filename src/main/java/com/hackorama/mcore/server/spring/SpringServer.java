package com.hackorama.mcore.server.spring;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.hackorama.mcore.common.HttpMethod;
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

    int port = 8080;
    String name;

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
    public void setRoutes(HttpMethod method, String path, Method handler) {
        Handler.getHandlerMap().get(method).put(path, handler);
        trackParamList(path);
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

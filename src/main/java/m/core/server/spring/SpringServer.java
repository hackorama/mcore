package m.core.server.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import m.core.http.Method;
import m.core.http.Request;
import m.core.http.Response;
import m.core.server.BaseServer;

/**
 * An HTTP server for REST API routes using Spring WebFlux.
 *
 * @see <a href="https://spring.io">spring.io</a>
 */
/*
 * DEVELOPER NOTES: Routes must be configured before server start, cannot modify
 * or remove routes after server start.
 */
public class SpringServer extends BaseServer {

    /**
     * Constructs a server with specified name.
     *
     * @param name the server name
     */
    public SpringServer(String name) {
        super(name);
    }

    /**
     * Constructs a server with specified name and port.
     *
     * @param name the server name
     * @param port the port server listens on
     */
    public SpringServer(String name, int port) {
        super(name, port);
    }

    @Override
    public void setRoutes(Method method, String path, Function<Request, Response> handler) {
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

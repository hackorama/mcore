package m.core.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.uri.UriTemplate;

import m.core.http.Method;
import m.core.http.Request;
import m.core.http.Response;

public abstract class BaseServer implements Server {

    protected String host = "0.0.0.0";
    protected String name = "mCore";
    protected Map<String, List<String>> paramListMap = new HashMap<>(); // used for matching paths
    protected int port = 8080;
    protected Map<Method, Map<String, Function<Request, Response>>> routeHandlerMap = new HashMap<>();

    public BaseServer() {
        init();
    }

    public BaseServer(String name) {
        this();
        this.name = name;
    }

    public BaseServer(String name, int port) {
        this(name);
        this.port = port;
    }

    public BaseServer(String name, String host, int port) {
        this(name, port);
        this.host = host;
    }

    protected String formatPathVariable(String path) {
        UriTemplate uriTemplate = new UriTemplate(path);
        Map<String, String> parameters = new HashMap<>();
        uriTemplate.getTemplateVariables().forEach(e -> {
            parameters.put(e, ":" + e);
        });
        UriBuilder builder = UriBuilder.fromPath(path);
        URI output = builder.buildFromMap(parameters);
        return output.toString();
    }

    @Override
    public String getHost() {
        return host;
    }

    protected String getMatchingPath(Map<String, Function<Request, Response>> paths, String path,
            Map<String, String> paramValues) {
        if (paths.containsKey(path)) {
            return path;
        }
        for (Entry<String, List<String>> entry : paramListMap.entrySet()) {
            String stored = entry.getKey();
            for (String paramName : entry.getValue()) {
                if (paramValues.containsKey(paramName)) {
                    stored = stored.replaceFirst(paramName, paramValues.get(paramName)); // TODO else break
                }
            }
            if (stored.equals(path)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPort() {
        return port;
    }

    protected void init() {
        Stream.of(Method.values()).forEach(e -> {
            routeHandlerMap.put(e, new HashMap<>());
        });
    }

    @Override
    public abstract void setRoutes(Method method, String path, Function<Request, Response> handler);

    @Override
    public void setRoutes(Map<Method, Map<String, Function<Request, Response>>> routeHandlerMap) {
        routeHandlerMap.forEach((method, route) -> {
            route.forEach((path, handler) -> {
                setRoutes(method, path, handler);
            });
        });
    }

    @Override
    public abstract boolean start();

    @Override
    public abstract void stop();

    protected void trackParamList(String path) {
        List<String> params = new ArrayList<String>(Arrays.asList(path.split("/"))).stream()
                .filter(e -> e.startsWith(":")).collect(Collectors.toList());
        if (!params.isEmpty()) {
            paramListMap.put(path, params);
        }
    }

}

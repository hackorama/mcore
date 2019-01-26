package com.hackorama.mcore.server.vertx;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.uri.UriTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.Util;
import com.hackorama.mcore.server.Server;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Vertx server implementation
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class VertxServer implements Server {

    private static final Logger logger = LoggerFactory.getLogger(VertxServer.class);

    private static Map<HttpMethod, Map<String, Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response>>> handlerMap = new HashMap<>();
    private static Map<String, List<String>> paramListMap = new HashMap<>(); // used for matching paths

    private transient Vertx vertx;
    private Router router;
    private int port = 8080;
    private String name;

    {
        handlerMap.put(HttpMethod.GET, new HashMap<>());
        handlerMap.put(HttpMethod.POST, new HashMap<>());
        handlerMap.put(HttpMethod.PUT, new HashMap<>());
        handlerMap.put(HttpMethod.DELETE, new HashMap<>());
    }

    private static Map<String, String> formatParams(Map<String, String> params) {
        Map<String, String> parameters = new HashMap<>();
        params.forEach((k, v) -> {
            if (!k.startsWith(":")) {
                parameters.put(":" + k, v);
            } else {
                parameters.put(k, v);
            }
        });
        return parameters;
    }

    public static String formatPathVariable(String path) {
        UriTemplate uriTemplate = new UriTemplate(path);
        Map<String, String> parameters = new HashMap<>();
        uriTemplate.getTemplateVariables().forEach(e -> {
            parameters.put(e, ":" + e);
        });
        UriBuilder builder = UriBuilder.fromPath(path);
        URI output = builder.buildFromMap(parameters);
        return output.toString();
    }

    private static String getMatchingPath(
            Map<String, Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response>> paths,
            String path, Map<String, String> paramValues) {
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

    public VertxServer() {
        vertx = Vertx.vertx();
        router = Router.router(vertx);
    }

    public VertxServer(String name) {
        this();
        this.name = name;
    }

    public VertxServer(String name, int port) {
        this(name);
        this.port = port;
    }

    private void activateRoutes() {
        router.route().handler(BodyHandler.create()); // Must be set before the routes
        handlerMap.get(HttpMethod.GET).keySet().forEach(path -> {
            router.get(path).handler(this::route);
        });
        handlerMap.get(HttpMethod.POST).keySet().forEach(path -> {
            router.post(path).handler(this::route);
        });
        handlerMap.get(HttpMethod.PUT).keySet().forEach(path -> {
            router.put(path).handler(this::route);
        });
        handlerMap.get(HttpMethod.DELETE).keySet().forEach(path -> {
            router.delete(path).handler(this::route);
        });
    }

    @Override
    public String getName() {
        return name;
    }

    private void route(RoutingContext routingContext) {
        logger.debug("Routing request {} on thread id {} thread name : {} ", routingContext.normalisedPath(),
                Thread.currentThread().getId(), Thread.currentThread().getName());
        com.hackorama.mcore.common.Request request = new com.hackorama.mcore.common.Request(
                routingContext.getBodyAsString(), routingContext.pathParams());
        String matchingPath = getMatchingPath(handlerMap.get(HttpMethod.valueOf(routingContext.request().rawMethod())),
                routingContext.normalisedPath(), formatParams(routingContext.pathParams()));
        if (matchingPath != null) {
            com.hackorama.mcore.common.Response response = (com.hackorama.mcore.common.Response) handlerMap
                    .get(HttpMethod.valueOf(routingContext.request().rawMethod())).get(matchingPath).apply(request);
            routingContext.response().setStatusCode(response.getStatus()).end(response.getBody());
        } else {
            routingContext.response().setStatusCode(HttpURLConnection.HTTP_NOT_FOUND)
                    .end(Util.toJsonString("message", "404 Not found"));
        }
    }

    @Override
    public void setRoutes(HttpMethod method, String path, Function<Request, Response> handler) {
        router.get(path).handler(this::route);
        String vertexPath = formatPathVariable(path);
        handlerMap.get(method).put(vertexPath, handler);
        trackParamList(vertexPath);
    }

    @Override
    public boolean start() {
        activateRoutes();
        logger.info("Starting vertx server {} on {}", name, port);
        vertx.deployVerticle(new AppVerticle(vertx, router, port));
        return true;
    }

    @Override
    public void stop() {
        if (vertx != null) {
            vertx.close();
            logger.info("Stopped vertx server {} on {}", name, port);
        }
    }

    private void trackParamList(String path) {
        List<String> params = new ArrayList<String>(Arrays.asList(path.split("/"))).stream()
                .filter(e -> e.startsWith(":")).collect(Collectors.toList());
        if (!params.isEmpty()) {
            paramListMap.put(path, params);
        }
    }

}

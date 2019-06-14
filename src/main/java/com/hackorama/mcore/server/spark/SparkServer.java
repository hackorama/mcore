package com.hackorama.mcore.server.spark;

import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;
import spark.Spark;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Util;
import com.hackorama.mcore.server.BaseServer;

public class SparkServer extends BaseServer {

    private static final Logger logger = LoggerFactory.getLogger(SparkServer.class);

    public SparkServer(String name) {
        super(name);
    }

    public SparkServer(String name, int port) {
        super(name, port);
    }

    private void activateRoutes() {
        routeHandlerMap.get(HttpMethod.GET).keySet().forEach( path -> {
            Spark.get(path, this::router);
        });
        routeHandlerMap.get(HttpMethod.POST).keySet().forEach( path -> {
            Spark.post(path, this::router);
        });
        routeHandlerMap.get(HttpMethod.PUT).keySet().forEach( path -> {
            Spark.put(path, this::router);
        });
        routeHandlerMap.get(HttpMethod.DELETE).keySet().forEach( path -> {
            Spark.delete(path, this::router);
        });
    }

    private Map<String, String> formatParams(Map<String, String> params) {
        Map<String, String> parameters = new HashMap<>();
        params.forEach((k, v) -> {
            if (k.startsWith(":")) {
                parameters.put(k.substring(1), v);
            } else {
                parameters.put(k, v);
            }
        });
        return parameters;
    }

    public String router(Request req, Response res)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        System.out.println(req.queryParamOrDefault("test", "def"));
        System.out.println(req.queryString());
        System.out.println(req.headers());
        com.hackorama.mcore.common.Request request = new com.hackorama.mcore.common.Request(req.body(), formatParams(req.params())); // TODO
        String matchingPath = getMatchingPath(routeHandlerMap.get(HttpMethod.valueOf(req.requestMethod())), req.pathInfo(),
                req.params());
        if (matchingPath != null) {
            com.hackorama.mcore.common.Response response = (com.hackorama.mcore.common.Response) routeHandlerMap
                    .get(HttpMethod.valueOf(req.requestMethod())).get(matchingPath).apply(request);
            res.status(response.getStatus());
            res.body(response.getBody());
        } else {
            res.status(HttpURLConnection.HTTP_NOT_FOUND);
            res.body(Util.toJsonString("message", "404 Not found"));
        }
        logger.debug("Routing request {} on thread id {} thread name : {} ",  req.pathInfo(), Thread.currentThread().getId(), Thread.currentThread().getName());
        return res.body();
    }

    @Override
    public void setRoutes(HttpMethod method, String path, Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response> handler) {
        String sparkPath = formatPathVariable(path);
        routeHandlerMap.get(method).put(sparkPath, handler);
        trackParamList(sparkPath);
    }

    @Override
    public boolean start() {
        Spark.port(port);
        activateRoutes();
        return true;
    }

    @Override
    public void stop() {
        Spark.awaitInitialization();
        Spark.stop();
    }

}

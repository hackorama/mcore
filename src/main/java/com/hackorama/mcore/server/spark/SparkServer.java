package com.hackorama.mcore.server.spark;

import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.sql.Timestamp;
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

import spark.Request;
import spark.Response;
import spark.Spark;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Util;
import com.hackorama.mcore.server.Server;

public class SparkServer implements Server {

    private static Map<HttpMethod, Map<String, Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response>>> handlerMap = new HashMap<>();
    private static Map<String, List<String>> paramListMap = new HashMap<>(); // used for matching paths

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
            if (k.startsWith(":")) {
                parameters.put(k.substring(1), v);
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

    private static String getMatchingPath(Map<String, Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response>> paths, String path, Map<String, String> paramValues) {
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

    public static String router(Request req, Response res)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        com.hackorama.mcore.common.Request request = new com.hackorama.mcore.common.Request(req.body(), formatParams(req.params())); // TODO
        String matchingPath = getMatchingPath(handlerMap.get(HttpMethod.valueOf(req.requestMethod())), req.pathInfo(),
                req.params());
        if (matchingPath != null) {
            com.hackorama.mcore.common.Response response = (com.hackorama.mcore.common.Response) handlerMap
                    .get(HttpMethod.valueOf(req.requestMethod())).get(matchingPath).apply(request);
            res.status(response.getStatus());
            res.body(response.getBody());
        } else {
            res.status(HttpURLConnection.HTTP_NOT_FOUND);
            res.body(Util.toJsonString("message", "404 Not found"));
        }
        System.out.println(new Timestamp(System.currentTimeMillis()) + " SPARK " +  req.pathInfo()  + " " + Thread.currentThread().getId() + " " + Thread.currentThread().getName());
        return res.body();
    }

    public SparkServer(String name) {
        this.name = name;
    }

    public SparkServer(String name, int port) {
        this.name = name;
        this.port = port;
    }

    private void activateRoutes() {
        handlerMap.get(HttpMethod.GET).keySet().forEach( path -> {
            Spark.get(path, SparkServer::router);
        });
        handlerMap.get(HttpMethod.POST).keySet().forEach( path -> {
            Spark.post(path, SparkServer::router);
        });
        handlerMap.get(HttpMethod.PUT).keySet().forEach( path -> {
            Spark.put(path, SparkServer::router);
        });
        handlerMap.get(HttpMethod.DELETE).keySet().forEach( path -> {
            Spark.delete(path, SparkServer::router);
        });
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setRoutes(HttpMethod method, String path, Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response> handler) {
        String sparkPath = formatPathVariable(path);
        handlerMap.get(method).put(sparkPath, handler);
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

    private void trackParamList(String path) {
        List<String> params = new ArrayList<String>(Arrays.asList(path.split("/"))).stream()
                .filter(e -> e.startsWith(":")).collect(Collectors.toList());
        if (!params.isEmpty()) {
            paramListMap.put(path, params);
        }
    }

}

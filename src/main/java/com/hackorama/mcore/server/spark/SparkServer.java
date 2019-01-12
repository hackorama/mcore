package com.hackorama.mcore.server.spark;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import spark.Request;
import spark.Response;
import spark.Spark;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Util;
import com.hackorama.mcore.server.Server;

public class SparkServer implements Server {

    int port = 8080;
    String name;

    private static Map<HttpMethod, Map<String, Method>> handlerMap = new HashMap<>();
    {
        handlerMap.put(HttpMethod.GET, new HashMap<>());
        handlerMap.put(HttpMethod.POST, new HashMap<>());
        handlerMap.put(HttpMethod.PUT, new HashMap<>());
        handlerMap.put(HttpMethod.DELETE, new HashMap<>());
    }
    private static Map<String, List<String>> paramListMap = new HashMap<>(); // used for matching paths

    public static String router(Request req, Response res)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // res.type("application/json");
        com.hackorama.mcore.common.Request request = new com.hackorama.mcore.common.Request(req.body(), req.params()); // TODO
        String matchingPath = getMatchingPath(handlerMap.get(HttpMethod.valueOf(req.requestMethod())), req.pathInfo(),
                req.params());
        if (matchingPath != null) {
            com.hackorama.mcore.common.Response response = (com.hackorama.mcore.common.Response) handlerMap
                    .get(HttpMethod.valueOf(req.requestMethod())).get(matchingPath).invoke(null, request);
            res.status(response.getStatus());
            res.body(response.getBody());
        } else {
            res.status(HttpURLConnection.HTTP_NOT_FOUND);
            res.body(Util.toJsonString("message", "404 Not found"));
        }
        return res.body();
    }

    private static String getMatchingPath(Map<String, Method> paths, String path, Map<String, String> paramValues) {
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

    public SparkServer(String name) {
        this.name = name;
    }

    public SparkServer(String name, int port) {
        this.name = name;
        this.port = port;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setRoutes(HttpMethod method, String path, Method handler) {
        handlerMap.get(method).put(path, handler);
        trackParamList(path);
        switch (method) {
        case GET:
            Spark.get(path, SparkServer::router);
            break;
        case POST:
            Spark.post(path, SparkServer::router);
            break;
        case PUT:
            Spark.put(path, SparkServer::router);
            break;
        case DELETE:
            Spark.delete(path, SparkServer::router);
            break;
        default:
            break;
        }
    }

    private void trackParamList(String path) {
        List<String> params = new ArrayList<String>(Arrays.asList(path.split("/"))).stream()
                .filter(e -> e.startsWith(":")).collect(Collectors.toList());
        if (!params.isEmpty()) {
            paramListMap.put(path, params);
        }
    }

    @Override
    public boolean start() {
        Spark.port(port);
        return true;
    };

    @Override
    public void stop() {
        Spark.awaitInitialization();
        Spark.stop();
    }

    @Override
    public void removeRoutes(String path) {
        handlerMap.get(HttpMethod.GET).entrySet().removeIf(e -> e.getKey().startsWith(path));
        handlerMap.get(HttpMethod.POST).entrySet().removeIf(e -> e.getKey().startsWith(path));
        handlerMap.get(HttpMethod.PUT).entrySet().removeIf(e -> e.getKey().startsWith(path));
        handlerMap.get(HttpMethod.DELETE).entrySet().removeIf(e -> e.getKey().startsWith(path));
    }

}

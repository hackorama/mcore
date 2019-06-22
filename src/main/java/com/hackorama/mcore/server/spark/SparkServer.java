package com.hackorama.mcore.server.spark;

import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.Cookie;

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
        routeHandlerMap.get(HttpMethod.GET).keySet().forEach(path -> {
            Spark.get(path, this::router);
        });
        routeHandlerMap.get(HttpMethod.POST).keySet().forEach(path -> {
            Spark.post(path, this::router);
        });
        routeHandlerMap.get(HttpMethod.PUT).keySet().forEach(path -> {
            Spark.put(path, this::router);
        });
        routeHandlerMap.get(HttpMethod.DELETE).keySet().forEach(path -> {
            Spark.delete(path, this::router);
        });
    }

    private void debug(Request req) {
        System.out.println("COOKIE:");
        req.cookies().forEach((k, v) -> {
            System.out.println(k + ":" + v);
        });
    }

    private Map<String, Cookie> formatCookies(Map<String, String> cookieMap) {
        ;
        Map<String, Cookie> cookies = new HashMap<>();
        cookieMap.forEach((k, v) -> {
            cookies.put(k, new Cookie(k, v));
        });
        return cookies;
    }

    private Map<String, List<String>> formatHeaders(Request request) {
        Map<String, List<String>> headers = new HashMap<>();
        request.headers().forEach(h -> {
            // Only single header value per key supported stored as single item list
            headers.put(h, new ArrayList<String>(Arrays.asList(request.headers(h))));
        });
        return headers;
    }

    private void formatNotFoundResponse(Response res) {
        res.status(HttpURLConnection.HTTP_NOT_FOUND);
        res.body(Util.toJsonString("message", "404 Not found"));
    }

    private Map<String, String> formatPathParams(Request request) {
        Map<String, String> params = new HashMap<>();
        request.params().forEach((k, v) -> {
            if (k.startsWith(":")) {
                params.put(k.substring(1), v);
            } else {
                params.put(k, v);
            }
        });
        return params;
    }

    private Map<String, List<String>> formatQueryParams(Request request) {
        Map<String, List<String>> params = new HashMap<>();
        request.queryMap().toMap().forEach((k, v) -> {
            params.put(k, Arrays.asList(v));
        });
        return params;
    }

    private com.hackorama.mcore.common.Request formatRequest(Request req) {
        return new com.hackorama.mcore.common.Request().setBody(req.body()).setPathParams(formatPathParams(req))
                .setQueryParams(formatQueryParams(req)).setHeaders(formatHeaders(req))
                .setCookies(formatCookies(req.cookies()));
    }

    private void formatResponse(com.hackorama.mcore.common.Response response, Response res) {
        response.getHeaders().forEach((k, v) -> {
            v.forEach(e -> {
                res.header(k, e);
            });
        });
        response.getCookies().forEach((k, v) -> {
            if (v.getDomain() != null) { // TODO Add check for all fields if needed
                res.cookie(v.getDomain(), v.getPath(), v.getName(), v.getValue(), v.getMaxAge(), v.getSecure(),
                        v.isHttpOnly());
            } else {
                res.cookie(v.getPath(), v.getName(), v.getValue(), v.getMaxAge(), v.getSecure(), v.isHttpOnly());
            }
        });
        res.status(response.getStatus());
        res.body(response.getBody());
    }

    public String router(Request req, Response res)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        debug(req);
        com.hackorama.mcore.common.Request request = formatRequest(req);
        String matchingPath = getMatchingPath(routeHandlerMap.get(HttpMethod.valueOf(req.requestMethod())),
                req.pathInfo(), req.params());
        if (matchingPath != null) {
            com.hackorama.mcore.common.Response response = (com.hackorama.mcore.common.Response) routeHandlerMap
                    .get(HttpMethod.valueOf(req.requestMethod())).get(matchingPath).apply(request);
            formatResponse(response, res);
        } else {
            formatNotFoundResponse(res);
        }
        logger.debug("Routing request {} on thread id {} thread name : {} ", req.pathInfo(),
                Thread.currentThread().getId(), Thread.currentThread().getName());
        return res.body();
    }

    @Override
    public void setRoutes(HttpMethod method, String path,
            Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response> handler) {
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

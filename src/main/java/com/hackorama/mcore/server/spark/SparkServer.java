package com.hackorama.mcore.server.spark;

import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
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
        Stream.of(HttpMethod.values()).forEach(e -> {
            routeHandlerMap.get(e).keySet().forEach(path -> {
                Spark.get(path, this::router);
            });
        });
    }

    private Map<String, List<Cookie>> formatCookies(Map<String, String> cookieMap) {
        Map<String, List<Cookie>> cookies = new HashMap<>();
        cookieMap.forEach((k, v) -> {
            Cookie cookie = new Cookie(k, v);
            if (cookies.containsKey(k)) {
                cookies.get(cookie.getName()).add(cookie);
            } else {
                List<Cookie> values = new ArrayList<Cookie>();
                values.add(cookie);
                cookies.put(cookie.getName(), values);
            }
        });
        return cookies;
    }

    private Map<String, List<String>> formatHeaders(Request request) {
        Map<String, List<String>> headers = new HashMap<>();
        // Only single header value per key exposed by spark Request interface,
        // using the raw HttpServletRequest instance to get multiple values
        Enumeration<String> names = request.raw().getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            headers.put(name, Collections.list(request.raw().getHeaders(name)));
        }
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
            v.forEach(e -> {
                if (e.getDomain() != null) { // TODO Add check for all fields if needed
                    res.cookie(e.getDomain(), e.getPath(), e.getName(), e.getValue(), e.getMaxAge(), e.getSecure(),
                            e.isHttpOnly());
                } else {
                    res.cookie(e.getPath(), e.getName(), e.getValue(), e.getMaxAge(), e.getSecure(), e.isHttpOnly());
                }
            });
        });
        res.status(response.getStatus());
        if (StringUtils.isEmpty(response.getBody())) {
            res.body(""); // Must set an empty string as body
        } else {
            res.body(response.getBody());
        }
    }

    public String router(Request req, Response res)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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

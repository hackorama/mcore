package com.hackorama.mcore.server.spring;

import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.ServerResponse.BodyBuilder;

import reactor.core.publisher.Mono;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.Util;

@Component
public class Handler {

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);

    private static Map<String, List<String>> paramListMap; // used for matching paths
    private static Map<HttpMethod, Map<String, Function<Request, Response>>> routeHandlerMap;

    static void setParamListMap(Map<String, List<String>> paramListMap) {
        Handler.paramListMap = paramListMap;
    }

    static void setRouteHandlerMap(Map<HttpMethod, Map<String, Function<Request, Response>>> routeHandlerMap) {
        Handler.routeHandlerMap = routeHandlerMap;
    }

    private Map<String, Cookie> formatCookies(ServerRequest req) {
        Map<String, Cookie> cookies = new HashMap<>();
        req.cookies().values().forEach(v -> {
            v.forEach(e -> {
                cookies.put(e.getName(), new Cookie(e.getName(), e.getValue()));
            });
        });
        System.out.println("COOKIE:");
        req.cookies().forEach((k, v) -> {
            System.out.println(k);
            v.forEach(e -> {
                System.out.println("  " + e.getName() + ":" + e.getValue());
            });
        });
        return cookies;
    }

    private Map<String, List<String>> formatHeaders(ServerRequest req) {
        Map<String, List<String>> headers = new HashMap<>();
        req.headers().asHttpHeaders().keySet().forEach(k -> {
            headers.put(k, req.headers().asHttpHeaders().get(k));
        });
        return headers;
    }

    Map<HttpMethod, Map<String, Function<Request, Response>>> getHandlerMap() {
        return Handler.routeHandlerMap;
    }

    private String getMatchingPath(Map<String, Function<Request, Response>> paths, String path,
            Map<String, String> paramValues) {
        Map<String, String> formattedParamValues = new HashMap<>(); // TODO improve
        paramValues.forEach((k, v) -> {
            formattedParamValues.put("{" + k + "}", v);
        });
        if (paths.containsKey(path)) {
            return path;
        }
        for (Entry<String, List<String>> entry : Handler.paramListMap.entrySet()) {
            String stored = entry.getKey();
            for (String paramName : entry.getValue()) {
                if (formattedParamValues.containsKey(paramName)) {
                    // TODO else break
                    stored = stored.replaceFirst(Pattern.quote(paramName), formattedParamValues.get(paramName));
                }
            }
            if (stored.equals(path)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Mono<ServerResponse> router(ServerRequest req) throws InterruptedException, ExecutionException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // TODO Use future get
        Request request = new Request(req.bodyToMono(String.class).toFuture().get()).setPathParams(req.pathVariables())
                .setQueryParams(req.queryParams()).setHeaders(formatHeaders(req)).setCookies(formatCookies(req));
        String matchingPath = getMatchingPath(Handler.routeHandlerMap.get(HttpMethod.valueOf(req.methodName())),
                req.path(), req.pathVariables());
        logger.debug("Routing request {} on thread id {} thread name : {} ", req.path(), Thread.currentThread().getId(),
                Thread.currentThread().getName());
        if (matchingPath != null) {
            Response response = (Response) Handler.routeHandlerMap.get(HttpMethod.valueOf(req.methodName()))
                    .get(matchingPath).apply(request);
            BodyBuilder res = ServerResponse.status(response.getStatus());
            return res.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(response.getBody()));
        } else {
            BodyBuilder res = ServerResponse.status(HttpURLConnection.HTTP_NOT_FOUND);
            return res.contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromObject(Util.toJsonString("message", "404 Not found")));
        }
    }

}

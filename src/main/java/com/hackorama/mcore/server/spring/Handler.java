package com.hackorama.mcore.server.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.ServerResponse.BodyBuilder;

import reactor.core.publisher.Mono;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Util;

@Component
public class Handler {

    private static Map<HttpMethod, Map<String, Method>> handlerMap = new HashMap<>();
    private static Map<String, List<String>> paramListMap = new HashMap<>(); // used for matching paths

    public static Map<HttpMethod, Map<String, Method>> getHandlerMap() {
        if (handlerMap.isEmpty()) {
            handlerMap.put(HttpMethod.GET, new HashMap<>());
            handlerMap.put(HttpMethod.POST, new HashMap<>());
            handlerMap.put(HttpMethod.PUT, new HashMap<>());
            handlerMap.put(HttpMethod.DELETE, new HashMap<>());
        }
        return handlerMap;
    }

    private static String getMatchingPath(Map<String, Method> paths, String path, Map<String, String> paramValues) {
        Map<String, String> formattedParamValues = new HashMap<>(); // TODO improve
        paramValues.forEach((k, v) -> {
            formattedParamValues.put("{" + k + "}", v);
        });
        if (paths.containsKey(path)) {
            return path;
        }
        for (Entry<String, List<String>> entry : paramListMap.entrySet()) {
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

    public static Map<String, List<String>> getParamListMap() {
        return paramListMap;
    }

    public Mono<ServerResponse> router(ServerRequest req) throws InterruptedException, ExecutionException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        com.hackorama.mcore.common.Request request = new com.hackorama.mcore.common.Request(
                req.bodyToMono(String.class).toFuture().get(), req.pathVariables()); // TODO future get
        String matchingPath = getMatchingPath(handlerMap.get(HttpMethod.valueOf(req.methodName())), req.path(),
                req.pathVariables());
        if (matchingPath != null) {
            com.hackorama.mcore.common.Response response = (com.hackorama.mcore.common.Response) handlerMap
                    .get(HttpMethod.valueOf(req.methodName())).get(matchingPath).invoke(null, request);
            BodyBuilder res = ServerResponse.status(response.getStatus());
            return res.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(response.getBody()));
        } else {
            BodyBuilder res = ServerResponse.status(HttpURLConnection.HTTP_NOT_FOUND);
            return res.contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromObject(Util.toJsonString("message", "404 Not found")));
        }
    }

}

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
import com.hackorama.mcore.common.Util;

@Component
public class Handler {

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);

    private static Map<HttpMethod, Map<String, Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response>>> handlerMap = new HashMap<>();
    private static Map<String, List<String>> paramListMap = new HashMap<>(); // used for matching paths

    public static Map<HttpMethod, Map<String, Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response>>> getHandlerMap() {
        if (handlerMap.isEmpty()) {
            handlerMap.put(HttpMethod.GET, new HashMap<>());
            handlerMap.put(HttpMethod.POST, new HashMap<>());
            handlerMap.put(HttpMethod.PUT, new HashMap<>());
            handlerMap.put(HttpMethod.DELETE, new HashMap<>());
        }
        return handlerMap;
    }

    private static String getMatchingPath(Map<String, Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response>> paths, String path, Map<String, String> paramValues) {
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
        logger.debug("Routing request {} on thread id {} thread name : {} ", req.path(), Thread.currentThread().getId(),
                Thread.currentThread().getName());
        if (matchingPath != null) {
            com.hackorama.mcore.common.Response response = (com.hackorama.mcore.common.Response) handlerMap
                    .get(HttpMethod.valueOf(req.methodName())).get(matchingPath).apply(request);
            BodyBuilder res = ServerResponse.status(response.getStatus());
            return res.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(response.getBody()));
        } else {
            BodyBuilder res = ServerResponse.status(HttpURLConnection.HTTP_NOT_FOUND);
            return res.contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromObject(Util.toJsonString("message", "404 Not found")));
        }
    }

}

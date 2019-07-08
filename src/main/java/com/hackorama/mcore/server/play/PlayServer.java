package com.hackorama.mcore.server.play;

import static play.mvc.Controller.*;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.Cookie;

import play.mvc.Http.Request;
import play.mvc.Result;
import play.routing.RoutingDsl;
import play.server.Server;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.Session;
import com.hackorama.mcore.server.BaseServer;

/**
 *
 * Play Framework Server Implementation
 *
 * NOTES: Alternatively could leverage OnRouteRequest override
 *
 * - https://www.playframework.com/documentation/2.1.0/JavaGlobal -
 * https://www.playframework.com/documentation/2.1.0/JavaInterceptors
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class PlayServer extends BaseServer {

    class MatchingPath {
        Map<String, String> params = new HashMap<>();
        String path;
    }

    private Server server;

    public PlayServer(String name) {
        super(name);
    }

    private Map<String, List<Cookie>> formatCookies(Request playRequest) {
        return null;
    }

    private Map<String, List<String>> formatHeaders(Request playRequest) {
        return null;
    }

    private Result formatNotFoundResponse() {
        return notFound("No matching path");
    }

    private Map<String, List<String>> formatQueryParams(Request request) {
        StringBuilder queryString = new StringBuilder("QUERYSTRING: ");
        request.queryString().forEach((k, v) -> {
            Arrays.stream(v).forEach(e -> {
                queryString.append(k).append(":").append(e).append(" ");
            });
        });
        return new HashMap<>();
    }

    private com.hackorama.mcore.common.Request formatRequest(Request playRequest) {
        return new com.hackorama.mcore.common.Request().setBody(playRequest.body().asText())
                .setQueryParams(formatQueryParams(playRequest)).setHeaders(formatHeaders(playRequest))
                .setCookies(formatCookies(playRequest)).setSession(formatSession(playRequest));
    }

    private Result formatResponse(Response response) {
        return ok(response.getBody());
    }

    private Session formatSession(Request playRequest) {
        return null;
    }

    private MatchingPath getMatchingPath(Map<String, Function<com.hackorama.mcore.common.Request, Response>> paths,
            String path) {
        MatchingPath matchingPath = new MatchingPath();
        if (paths.containsKey(path)) {
            matchingPath.path = path;
            return matchingPath;
        }
        paths.keySet().forEach(p -> {
            List<String> potential = new ArrayList<>();
            Paths.get(p).forEach(e -> potential.add(e.toString()));
            List<String> target = new ArrayList<>();
            Paths.get(path).forEach(e -> target.add(e.toString()));

            if (potential.size() == target.size()) {
                boolean match = true;
                for (int i = 0; i < potential.size(); i++) {
                    if (potential.get(i).startsWith(":")) {
                        matchingPath.params.put(potential.get(i).substring(1), target.get(i));
                    } else if (!potential.get(i).equals(target.get(i))) {
                        match = false;
                    }
                }
                if (match) {
                    matchingPath.path = path;
                    return;
                }
            }

        });
        return matchingPath;
    }

    private Result route(Request playRequest) {
        com.hackorama.mcore.common.Request request = formatRequest(playRequest);
        MatchingPath matchingPath = getMatchingPath(routeHandlerMap.get(HttpMethod.valueOf(playRequest.method())),
                playRequest.path());
        System.out.println(matchingPath.path);
        matchingPath.params.forEach((k, v) -> {
            System.out.println(" " + k + ":" + v);
        });
        request.setPathParams(matchingPath.params);
        if (matchingPath.path != null) {
            com.hackorama.mcore.common.Response response = (com.hackorama.mcore.common.Response) routeHandlerMap
                    .get(HttpMethod.valueOf(playRequest.method())).get(matchingPath.path).apply(request);
            updateSession(playRequest, request.getSession());
            response.setBody("Found " + matchingPath.path + ", response not implemented yet");
            return formatResponse(response);
        } else {
            return formatNotFoundResponse();
        }
    }

    @Override
    public void setRoutes(HttpMethod method, String path,
            Function<com.hackorama.mcore.common.Request, Response> handler) {
        routeHandlerMap.get(method).put(path, handler); // Move to super
        trackParamList(path);
    }

    @Override
    public boolean start() {
        start(port);
        return true;
    }

    public void start(int port) {
        server = Server.forRouter(port,
                (components) -> RoutingDsl.fromComponents(components).GET("/*path").routeTo(path -> {
                    return route(request());
                }).POST("/*path").routeTo(path -> {
                    return route(request());
                }).DELETE("/*path").routeTo(path -> {
                    return route(request());
                }).build());
        System.out.println(server.httpPort());
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }

    private void updateSession(Request playRequest, Session session) {

    }

}

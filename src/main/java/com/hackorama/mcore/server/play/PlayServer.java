package com.hackorama.mcore.server.play;

import static play.mvc.Controller.*;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;

import play.mvc.Http;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.routing.RoutingDsl;
import play.server.Server;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.Session;
import com.hackorama.mcore.common.Util;
import com.hackorama.mcore.server.BaseServer;

/**
 *
 * Play Framework Server Implementation
 * <p>
 * NOTES: Alternatively could leverage OnRouteRequest override
 * <p>
 * <ul>
 * <li>https://www.playframework.com/documentation/2.1.0/JavaGlobal
 * <li>https://www.playframework.com/documentation/2.1.0/JavaInterceptors
 * </ul>
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class PlayServer extends BaseServer {

    static class MatchingPath {
        Map<String, String> params = new HashMap<>();
        String path;
    }

    private Server server;

    public PlayServer(String name) {
        super(name);
    }

    public PlayServer(String name, int port) {
        super(name, port);
    }

    private Map<String, List<Cookie>> formatCookies(Request playRequest) {
        Map<String, List<Cookie>> cookies = new HashMap<>();
        playRequest.cookies().forEach(e -> {
            // TODO Check missing sameSite property
            Cookie cookie = new Cookie(e.name(), e.value());
            if (e.domain() != null) { // TODO Look up cookie specs and document
                cookie.setDomain(e.domain());
            }
            cookie.setHttpOnly(e.httpOnly());
            cookie.setMaxAge(e.maxAge());
            cookie.setPath(e.path());
            cookie.setSecure(e.secure());
            // TODO : Move this as helper to BaseServer
            if (cookies.containsKey(cookie.getName())) {
                cookies.get(cookie.getName()).add(cookie);
            } else {
                List<Cookie> values = new ArrayList<Cookie>();
                values.add(cookie);
                cookies.put(cookie.getName(), values);
            }
        });
        return null;
    }

    private Map<String, List<String>> formatHeaders(Request playRequest) {
        return playRequest.getHeaders().toMap();
    }

    // TODO Move message to BaseServer
    private Result formatNotFoundResponse() {
        return notFound(Util.toJsonString("message", "404 Not found"));
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
        Result result = ok(response.getBody());
        for (Entry<String, List<String>> header : response.getHeaders().entrySet()) {
            for (String value : header.getValue()) {
                result = result.withHeader(header.getKey(), value);
            }
        }
        for (Entry<String, List<Cookie>> cookies : response.getCookies().entrySet()) {
            for (Cookie cookie : cookies.getValue()) {
                Http.Cookie httpCookie = Http.Cookie.builder(cookie.getName(), cookie.getValue())
                        .withDomain(cookie.getDomain()).withPath(cookie.getPath()).withSecure(cookie.getSecure())
                        .withHttpOnly(cookie.isHttpOnly()).withMaxAge(Duration.ofSeconds(cookie.getMaxAge())).build();
                result = result.withCookies(httpCookie);
            }
        }
        return result;
    }

    // TODO : Session configuration
    // https://www.playframework.com/documentation/2.7.x/SettingsSession
    private Session formatSession(Request playRequest) {
        play.mvc.Http.Session playSession = playRequest.asScala().session().asJava();
        Session session = new Session().setId("PLAY_SESSION"); // No id from Play Session
        playSession.forEach((k, v) -> {
            session.setAttribute(k, v);
        });
        return session;
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
                }).PUT("/*path").routeTo(path -> {
                    return route(request());
                }).PATCH("/*path").routeTo(path -> {
                    return route(request());
                }).OPTIONS("/*path").routeTo(path -> {
                    return route(request());
                }).HEAD("/*path").routeTo(path -> {
                    return route(request());
                }).build());
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }

    private void updateSession(@Nonnull Request playRequest, @Nullable Session session) {
        if (session == null) {
            return;
        }
        assert (StringUtils.equals("PLAY_SESSION", session.getId())); // No id from Play Session
        play.mvc.Http.Session playSession = playRequest.asScala().session().asJava();
        session.getAttributes().forEach((k, v) -> {

            playSession.putIfAbsent(k, v.toString());
        });
    }

}

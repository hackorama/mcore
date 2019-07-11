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
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;

import play.http.HttpEntity;
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
            if (e.maxAge() != null) { // TODO Handle transient cookie
                cookie.setMaxAge(e.maxAge());
            }
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
        return cookies;
    }

    private Map<String, List<String>> formatHeaders(Request playRequest) {
        return playRequest.getHeaders().toMap();
    }

    // TODO Move message to BaseServer
    private Result formatNotFoundResponse() {
        return notFound(Util.toJsonString("message", "404 Not found"));
    }

    private Map<String, List<String>> formatQueryParams(Request request) {
        Map<String, List<String>> params = new HashMap<>();
        request.queryString().forEach((k, v) -> {
            params.put(k, Arrays.asList(v));
        });
        return params;
    }

    private com.hackorama.mcore.common.Request formatRequest(Request playRequest) {
        return new com.hackorama.mcore.common.Request().setBody(playRequest.body().asText())
                .setQueryParams(formatQueryParams(playRequest)).setHeaders(formatHeaders(playRequest))
                .setCookies(formatCookies(playRequest)).setSession(formatSession(playRequest));
    }

    private Result formatResponse(Response response) {
        // TODO FIXME Charset helper
        // TODO Must set an empty string as body
        Result result = response.getBody() == null
                ? new Result(response.getStatus(), HttpEntity.fromString("", "UTF-8"))
                : new Result(response.getStatus(), HttpEntity.fromString(response.getBody(), "UTF-8"));
        for (Entry<String, List<String>> header : response.getHeaders().entrySet()) {
            // TODO Check Play API : Sending multi-value as a comma separated single value
            result = result.withHeader(header.getKey(), StringUtils.join(header.getValue().toArray(), ","));
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
        paths.keySet().forEach(mappedPath -> {
            List<String> mappedPathElements = new ArrayList<>();
            Paths.get(mappedPath).forEach(e -> mappedPathElements.add(e.toString()));
            List<String> pathElements = new ArrayList<>();
            Paths.get(path).forEach(e -> pathElements.add(e.toString()));

            if (mappedPathElements.size() == pathElements.size()) {
                boolean match = true;
                for (int i = 0; i < mappedPathElements.size(); i++) {
                    if (mappedPathElements.get(i).startsWith("{") && mappedPathElements.get(i).endsWith("}")) {
                        matchingPath.params.put(
                                mappedPathElements.get(i).substring(1, mappedPathElements.get(i).length() - 1),
                                pathElements.get(i));
                    } else if (!mappedPathElements.get(i).equals(pathElements.get(i))) {
                        match = false;
                    }
                }
                if (match) {
                    matchingPath.path = mappedPath;
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
        request.setPathParams(matchingPath.params);
        if (matchingPath.path != null) {
            com.hackorama.mcore.common.Response response = (com.hackorama.mcore.common.Response) routeHandlerMap
                    .get(HttpMethod.valueOf(playRequest.method())).get(matchingPath.path).apply(request);
            updateSession(playRequest, request.getSession());
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

    // TODO Move to super as common for :param and {param} types
    @Override
    protected void trackParamList(String path) {
        List<String> params = new ArrayList<String>(Arrays.asList(path.split("/"))).stream()
                .filter(e -> e.startsWith("{") && e.endsWith("}")).collect(Collectors.toList());
        if (!params.isEmpty()) {
            paramListMap.put(path, params);
        }
    }

    private void updateSession(@Nonnull Request playRequest, @Nullable Session session) {
        if (session == null) {
            return;
        }
        assert (StringUtils.equals("PLAY_SESSION", session.getId())); // No id from Play Session
        play.mvc.Http.Session playSession = playRequest.asScala().session().asJava();
        session.getAttributes().forEach((k, v) -> {
            playSession.put(k, v.toString());
        });
        // TODO FIXME PERF Concurrent modification remove double loop
        List<String> keysToRemove = new ArrayList<>();
        playSession.keySet().forEach(e -> { // Removed attributes
            if (!session.getAttributes().keySet().contains(e)) {
                keysToRemove.add(e);
            }
        });
        keysToRemove.forEach(e -> {
                playSession.remove(e);
        });
        if (session.invalid()) { // TODO If invalid then don't set attributes ?
            playSession.clear();
        }
    }

}

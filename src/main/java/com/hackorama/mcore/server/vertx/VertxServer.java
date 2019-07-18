package com.hackorama.mcore.server.vertx;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hackorama.mcore.common.Util;
import com.hackorama.mcore.http.Method;
import com.hackorama.mcore.http.Request;
import com.hackorama.mcore.http.Response;
import com.hackorama.mcore.server.BaseServer;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

/**
 * Vertx server implementation
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class VertxServer extends BaseServer {

    private static final Logger logger = LoggerFactory.getLogger(VertxServer.class);

    private Router router;
    private transient Vertx vertx;

    public VertxServer() {
        super();
    }

    public VertxServer(String name) {
        super(name);
    }

    public VertxServer(String name, int port) {
        super(name, port);
    }

    private void activateRoutes() {
        router.route().handler(CookieHandler.create()); // Enable cookies for all paths, must be set before session
                                                        // handler
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx))); // Enable sessions
        router.route().handler(BodyHandler.create()); // Must be set before the routes
        Stream.of(Method.values()).forEach(e -> {
            routeHandlerMap.get(e).keySet().forEach(path -> {
                router.get(path).handler(this::route);
            });
        });
    }

    private io.vertx.ext.web.Cookie convertCookie(Cookie cookie) {
        io.vertx.ext.web.Cookie responseCookie = io.vertx.ext.web.Cookie.cookie(cookie.getName(), cookie.getValue());
        // TODO Check missing isChanged, isFromUseragent properties
        responseCookie.setDomain(cookie.getDomain());
        responseCookie.setHttpOnly(cookie.isHttpOnly());
        if (cookie.getMaxAge() > 0) { // TODO Check the cookie spec
            responseCookie.setMaxAge(cookie.getMaxAge());
        }
        responseCookie.setPath(cookie.getPath());
        responseCookie.setSecure(cookie.getSecure());
        return responseCookie;
    }

    private void debug(RoutingContext routingContext) {
        logger.debug("Routing request {} on thread id {} thread name : {} ", routingContext.normalisedPath(),
                Thread.currentThread().getId(), Thread.currentThread().getName());
    }

    private Map<String, List<String>> fomatQueryParams(MultiMap queryParams) {
        Map<String, List<String>> params = new HashMap<>();
        queryParams.names().forEach(k -> {
            params.put(k, queryParams.getAll(k));
        });
        return params;
    }

    private Map<String, List<Cookie>> formatCookies(RoutingContext routingContext) {
        Map<String, List<Cookie>> cookies = new HashMap<>();
        routingContext.cookies().forEach(e -> {
            Cookie cookie = new Cookie(e.getName(), e.getValue());
            cookie.setPath(e.getPath());
            if (e.getDomain() != null) { // TODO Look up cookie specs and document
                cookie.setDomain(e.getDomain());
            }
            // TODO FIXME Add domain, path, age, secure attributes
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

    private Map<String, List<String>> formatHeaders(HttpServerRequest httpServerRequest) {
        Map<String, List<String>> headers = new HashMap<>();
        httpServerRequest.headers().names().forEach(k -> {
            headers.put(k, httpServerRequest.headers().getAll(k));
        });
        return headers;
    }

    private void formatNotFoundResponse(RoutingContext routingContext) {
        routingContext.response().setStatusCode(HttpURLConnection.HTTP_NOT_FOUND)
                .end(Util.toJsonString("message", "404 Not found"));
    }

    private Map<String, String> formatParams(Map<String, String> params) {
        Map<String, String> parameters = new HashMap<>();
        params.forEach((k, v) -> {
            if (!k.startsWith(":")) {
                parameters.put(":" + k, v);
            } else {
                parameters.put(k, v);
            }
        });
        return parameters;
    }

    private Request formatRequest(RoutingContext routingContext) {
        return new com.hackorama.mcore.http.Request(routingContext.getBodyAsString())
                .setPathParams(routingContext.pathParams())
                .setQueryParams(fomatQueryParams(routingContext.queryParams()))
                .setHeaders(formatHeaders(routingContext.request())).setCookies(formatCookies(routingContext))
                .setSession(formatSession(routingContext.session()));
    }

    private void formatResponse(Response response, RoutingContext routingContext) {
        MultiMap headers = MultiMap.caseInsensitiveMultiMap();
        response.getHeaders().forEach((k, v) -> {
            v.forEach(e -> {
                headers.add(k, e);
            });
        });
        response.getCookies().forEach((k, v) -> {
            v.forEach(e -> {
                routingContext.addCookie(convertCookie(e));
            });
        });
        routingContext.response().headers().addAll(headers);
        if (StringUtils.isEmpty(response.getBody())) {
            routingContext.response().setStatusCode(response.getStatus()).end();
        } else {
            routingContext.response().setStatusCode(response.getStatus()).end(response.getBody());
        }
    }

    private @Nullable com.hackorama.mcore.http.Session formatSession(@Nullable Session vSession) {
        // Could be null without a session handler
        if (vSession == null) { // TODO Check isDestroyed and isEmpty
            return null;
        }
        com.hackorama.mcore.http.Session session = new com.hackorama.mcore.http.Session().setId(vSession.id())
                .setLastAccessedTime(vSession.lastAccessed()).setMaxInactiveInterval(vSession.timeout());
        vSession.data().forEach((k, v) -> {
            session.setAttribute(k, v);
        });
        return session;
    }

    @Override
    protected void init() {
        super.init();
        vertx = Vertx.vertx();
        router = Router.router(vertx);
    }

    private void route(RoutingContext routingContext) {
        debug(routingContext);
        com.hackorama.mcore.http.Request request = formatRequest(routingContext);
        String matchingPath = getMatchingPath(
                routeHandlerMap.get(Method.valueOf(routingContext.request().rawMethod())),
                routingContext.normalisedPath(), formatParams(routingContext.pathParams()));
        if (matchingPath != null) {
            com.hackorama.mcore.http.Response response = (com.hackorama.mcore.http.Response) routeHandlerMap
                    .get(Method.valueOf(routingContext.request().rawMethod())).get(matchingPath).apply(request);
            updateSession(routingContext.session(), request.getSession());
            formatResponse(response, routingContext);
        } else {
            formatNotFoundResponse(routingContext);
        }
    }

    @Override
    public void setRoutes(Method method, String path, Function<Request, Response> handler) {
        String vertexPath = formatPathVariable(path);
        routeHandlerMap.get(method).put(vertexPath, handler);
        trackParamList(vertexPath);
    }

    @Override
    public boolean start() {
        activateRoutes();
        logger.info("Starting vertx server {} on {}", name, port);
        vertx.deployVerticle(new AppVerticle(vertx, router, port));
        return true;
    }

    @Override
    public void stop() {
        if (vertx != null) {
            vertx.close();
            logger.info("Stopped vertx server {} on {}", name, port);
        }
    }

    private void updateSession(@Nullable Session vSession, @Nullable com.hackorama.mcore.http.Session session) {
        if (session == null || vSession == null) {
            return;
        }
        assert (StringUtils.equals(vSession.id(), session.getId()));
        // TODO PERF Improve the loops
        session.getAttributes().forEach((k, v) -> { // Updated/Old/New attributes
            vSession.put(k, v);
        });
        vSession.data().keySet().forEach(e -> { // Removed attributes
            if (!session.getAttributes().keySet().contains(e)) {
                vSession.remove(e);
            }
        });
        if (session.invalid()) {
            vSession.destroy();
        }

    }

}

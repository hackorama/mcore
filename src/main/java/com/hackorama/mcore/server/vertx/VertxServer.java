package com.hackorama.mcore.server.vertx;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.Util;
import com.hackorama.mcore.server.BaseServer;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;

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
        router.route().handler(CookieHandler.create()); // enable cookies for all paths
        router.route().handler(BodyHandler.create()); // Must be set before the routes
        routeHandlerMap.get(HttpMethod.GET).keySet().forEach(path -> {
            router.get(path).handler(this::route);
        });
        routeHandlerMap.get(HttpMethod.POST).keySet().forEach(path -> {
            router.post(path).handler(this::route);
        });
        routeHandlerMap.get(HttpMethod.PUT).keySet().forEach(path -> {
            router.put(path).handler(this::route);
        });
        routeHandlerMap.get(HttpMethod.DELETE).keySet().forEach(path -> {
            router.delete(path).handler(this::route);
        });
    }

    private io.vertx.ext.web.Cookie convertCookie(Cookie cookie) {
        io.vertx.ext.web.Cookie responseCookie = io.vertx.ext.web.Cookie.cookie(cookie.getName(), cookie.getValue());
        // TODO Check missing ischanged, isfromuseragent properties
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

    private Map<String, Cookie> formatCookies(RoutingContext routingContext) {
        Map<String, Cookie> cookies = new HashMap<>();
        routingContext.cookies().forEach(e -> {
            Cookie cookie = new Cookie(e.getName(), e.getValue());
            cookie.setPath(e.getPath());
            if(e.getDomain() != null) { // TODO Look up cookie specs and document
            cookie.setDomain(e.getDomain());
            }
            cookies.put(e.getName(), cookie);
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
        return new com.hackorama.mcore.common.Request(routingContext.getBodyAsString())
                .setPathParams(routingContext.pathParams())
                .setQueryParams(fomatQueryParams(routingContext.queryParams()))
                .setHeaders(formatHeaders(routingContext.request())).setCookies(formatCookies(routingContext));
    }

    private void formatResponse(Response response, RoutingContext routingContext) {
        MultiMap headers = MultiMap.caseInsensitiveMultiMap();
        response.getHeaders().forEach((k, v) -> {
            v.forEach(e -> {
                headers.add(k, e);
            });
        });
        response.getCookies().forEach((k, v) -> {
            routingContext.addCookie(convertCookie(v));
        });
        routingContext.response().headers().addAll(headers);
        routingContext.response().setStatusCode(response.getStatus()).end(response.getBody());
    }

    @Override
    protected void init() {
        super.init();
        vertx = Vertx.vertx();
        router = Router.router(vertx);
    }

    private void route(RoutingContext routingContext) {
        debug(routingContext);
        com.hackorama.mcore.common.Request request = formatRequest(routingContext);
        String matchingPath = getMatchingPath(
                routeHandlerMap.get(HttpMethod.valueOf(routingContext.request().rawMethod())),
                routingContext.normalisedPath(), formatParams(routingContext.pathParams()));
        if (matchingPath != null) {
            com.hackorama.mcore.common.Response response = (com.hackorama.mcore.common.Response) routeHandlerMap
                    .get(HttpMethod.valueOf(routingContext.request().rawMethod())).get(matchingPath).apply(request);
            formatResponse(response, routingContext);
        } else {
            formatNotFoundResponse(routingContext);
        }
    }

    @Override
    public void setRoutes(HttpMethod method, String path, Function<Request, Response> handler) {
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

}

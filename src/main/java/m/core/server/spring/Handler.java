package m.core.server.spring;

import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.ServerResponse.BodyBuilder;
import org.springframework.web.server.WebSession;

import reactor.core.publisher.Mono;

import m.core.common.Util;
import m.core.http.Method;
import m.core.http.Request;
import m.core.http.Response;
import m.core.http.Session;

@Component
class Handler {

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);

    private static Map<String, List<String>> paramListMap; // used for matching paths
    private static Map<Method, Map<String, Function<Request, Response>>> routeHandlerMap;

    static void setParamListMap(Map<String, List<String>> paramListMap) {
        Handler.paramListMap = paramListMap;
    }

    static void setRouteHandlerMap(Map<Method, Map<String, Function<Request, Response>>> routeHandlerMap) {
        Handler.routeHandlerMap = routeHandlerMap;
    }

    private ResponseCookie formatCookie(Cookie cookie) {
        // TODO Check missing samesite property
        return ResponseCookie.from(cookie.getName(), cookie.getValue()).domain(cookie.getDomain())
                .httpOnly(cookie.isHttpOnly()).maxAge(cookie.getMaxAge()).path(cookie.getPath())
                .secure(cookie.getSecure()).build();
    }

    private Map<String, List<Cookie>> formatCookies(ServerRequest req) {
        Map<String, List<Cookie>> cookies = new HashMap<>();
        req.cookies().keySet().forEach(k -> {
            List<Cookie> values = new ArrayList<>();
            req.cookies().get(k).forEach(e -> {
                // NOTE No domain, path, age, secure attributes
                values.add(new Cookie(e.getName(), e.getValue()));
            });
            cookies.put(k, values);
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

    private Mono<ServerResponse> formatNotFoundResponse() {
        BodyBuilder res = ServerResponse.status(HttpURLConnection.HTTP_NOT_FOUND);
        return res.contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(Util.toJsonString("message", "404 Not found")));
    }

    private Request formatRequest(ServerRequest req) throws InterruptedException, ExecutionException {
        // TODO Use future get
        return new Request(req.bodyToMono(String.class).toFuture().get()).setPathParams(req.pathVariables())
                .setQueryParams(req.queryParams()).setHeaders(formatHeaders(req)).setCookies(formatCookies(req))
                .setSession(formatSession(req.session()));
    }

    private Mono<ServerResponse> formatResponse(Response response) {
        HttpHeaders responseHeaders = new HttpHeaders();
        response.getHeaders().forEach((k, v) -> {
            v.forEach(e -> {
                responseHeaders.add(k, e);
            });
        });
        MultiValueMap<String, ResponseCookie> responseCookies = new LinkedMultiValueMap<>();
        response.getCookies().forEach((k, v) -> {
            v.forEach(e -> {
                responseCookies.add(k, formatCookie(e));
            });
        });
        BodyBuilder builder = ServerResponse.status(response.getStatus());

        Mono<ServerResponse> resp = null;
        if (StringUtils.isEmpty(response.getBody())) {
            resp = builder.contentType(MediaType.APPLICATION_JSON).headers(headers -> headers.addAll(responseHeaders))
                    .cookies(cookies -> cookies.addAll(responseCookies)).build();
        } else {
            resp = builder.contentType(MediaType.APPLICATION_JSON).headers(headers -> headers.addAll(responseHeaders))
                    .cookies(cookies -> cookies.addAll(responseCookies))
                    .body(BodyInserters.fromObject(response.getBody()));
        }
        return resp;
    }

    private @Nonnull Session formatSession(@Nonnull Mono<WebSession> webSessionMono) {
        Session session = new Session();
        webSessionMono.subscribe(webSession -> {
            session.setId(webSession.getId()).setLastAccessedTime(webSession.getLastAccessTime().toEpochMilli())
                    .setMaxInactiveInterval(webSession.getMaxIdleTime().getSeconds());
            webSession.getAttributes().forEach((k, v) -> {
                session.setAttribute(k, v);
            });
        });
        return session;
    }

    Map<Method, Map<String, Function<Request, Response>>> getHandlerMap() {
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

    Mono<ServerResponse> router(ServerRequest springRequest) throws InterruptedException, ExecutionException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Request request = formatRequest(springRequest);
        String matchingPath = getMatchingPath(
                Handler.routeHandlerMap.get(Method.valueOf(springRequest.methodName())), springRequest.path(),
                springRequest.pathVariables());
        logger.debug("Routing request {} on thread id {} thread name : {} ", springRequest.path(),
                Thread.currentThread().getId(), Thread.currentThread().getName());
        if (matchingPath != null) {
            Response response = (Response) Handler.routeHandlerMap.get(Method.valueOf(springRequest.methodName()))
                    .get(matchingPath).apply(request);
            updateSession(springRequest.session(), request.getSession());
            return formatResponse(response);
        } else {
            return formatNotFoundResponse();
        }
    }

    private void updateSession(@Nonnull Mono<WebSession> springSession, @Nullable Session session) {
        if (session == null) {
            return;
        }
        springSession.subscribe(webSession -> {
            assert (StringUtils.equals(webSession.getId(), session.getId()));
            // TODO PERF Improve the loops
            session.getAttributes().forEach((k, v) -> { // Updated/Old/New attributes
                webSession.getAttributes().put(k, v);
            });
            webSession.getAttributes().keySet().forEach(e -> { // Removed attributes
                if (!session.getAttributes().keySet().contains(e)) {
                    webSession.getAttributes().remove(e);
                }
            });
            if (session.invalid()) {
                webSession.invalidate();
            }
        });
    }

}

package m.core.server.spark;

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;
import spark.Spark;

import m.core.common.Util;
import m.core.http.Method;
import m.core.http.Session;
import m.core.server.BaseServer;

/**
 * An HTTP server for REST API routes using Spark.
 *
 * @see <a href="http://sparkjava.com">sparkjava.com</a>
 */
public class SparkServer extends BaseServer {

    private static final Logger logger = LoggerFactory.getLogger(SparkServer.class);

    /**
     * Constructs a server with specified name.
     *
     * @param name the server name
     */
    public SparkServer(String name) {
        super(name);
    }

    /**
     * Constructs a server with specified name and port.
     *
     * @param name the server name
     * @param port the port server listens on
     */
    public SparkServer(String name, int port) {
        super(name, port);
    }

    private void activateRoutes() {
        Stream.of(Method.values()).forEach(e -> {
            routeHandlerMap.get(e).keySet().forEach(path -> {
                switch (e) {
                case GET:
                    Spark.get(path, this::router);
                    break;
                case DELETE:
                    Spark.delete(path, this::router);
                    break;
                case HEAD:
                    Spark.head(path, this::router);
                    break;
                case OPTIONS:
                    Spark.options(path, this::router);
                    break;
                case PATCH:
                    Spark.patch(path, this::router);
                    break;
                case POST:
                    Spark.post(path, this::router);
                    break;
                case PUT:
                    Spark.put(path, this::router);
                    break;
                case TRACE:
                    Spark.trace(path, this::router);
                    break;
                default:
                    // TODO: Log error
                    break;
                }
            });
        });
    }

    private Map<String, List<Cookie>> formatCookies(Map<String, String> cookieMap) {
        Map<String, List<Cookie>> cookies = new HashMap<>();
        cookieMap.forEach((k, v) -> {
            // NOTE No domain, path, age, secure attributes
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

    private m.core.http.Request formatRequest(Request req) {
        return new m.core.http.Request().setBody(req.body()).setPathParams(formatPathParams(req))
                .setQueryParams(formatQueryParams(req)).setHeaders(formatHeaders(req))
                .setCookies(formatCookies(req.cookies())).setSession(formatSession(req.raw().getSession()));
    }

    private void formatResponse(m.core.http.Response response, Response sparkResponse) {
        response.getHeaders().forEach((k, v) -> {
            v.forEach(e -> {
                sparkResponse.header(k, e);
            });
        });
        response.getCookies().forEach((k, v) -> {
            v.forEach(e -> {
                if (e.getDomain() != null) { // TODO Add check for all fields if needed
                    sparkResponse.cookie(e.getDomain(), e.getPath(), e.getName(), e.getValue(), e.getMaxAge(),
                            e.getSecure(), e.isHttpOnly());
                } else {
                    sparkResponse.cookie(e.getPath(), e.getName(), e.getValue(), e.getMaxAge(), e.getSecure(),
                            e.isHttpOnly());
                }
            });
        });
        sparkResponse.status(response.getStatus());
        if (StringUtils.isEmpty(response.getBody())) {
            sparkResponse.body(""); // Must set an empty string as body
        } else {
            sparkResponse.body(response.getBody());
        }
    }

    private @Nonnull Session formatSession(@Nonnull HttpSession sparkSession) {
        Session session = new Session().setId(sparkSession.getId())
                .setLastAccessedTime(sparkSession.getLastAccessedTime())
                .setMaxInactiveInterval(sparkSession.getMaxInactiveInterval());
        Collections.list(sparkSession.getAttributeNames()).forEach(e -> { // TODO PERF Handle large enumeration size ?
            session.setAttribute(e, sparkSession.getAttribute(e));
        });
        return session;
    }

    private String router(Request sparkRequest, Response sparkResponse)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        m.core.http.Request request = formatRequest(sparkRequest);
        String matchingPath = getMatchingPath(routeHandlerMap.get(Method.valueOf(sparkRequest.requestMethod())),
                sparkRequest.pathInfo(), sparkRequest.params());
        if (matchingPath != null) {
            m.core.http.Response response = (m.core.http.Response) routeHandlerMap
                    .get(Method.valueOf(sparkRequest.requestMethod())).get(matchingPath).apply(request);
            updateSession(sparkRequest.session(), request.getSession());
            formatResponse(response, sparkResponse);
        } else {
            formatNotFoundResponse(sparkResponse);
        }
        logger.debug("Routing request {} on thread id {} thread name : {} ", sparkRequest.pathInfo(),
                Thread.currentThread().getId(), Thread.currentThread().getName());
        return sparkResponse.body();
    }

    @Override
    public void setRoutes(Method method, String path, Function<m.core.http.Request, m.core.http.Response> handler) {
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
        Spark.awaitStop();
    }

    private void updateSession(@Nonnull spark.Session sparkSession, @Nullable Session session) {
        if (session == null) {
            return;
        }
        assert (StringUtils.equals(sparkSession.id(), session.getId()));
        // TODO PERF Improve the loops
        session.getAttributes().forEach((k, v) -> { // Updated/Old/New attributes
            sparkSession.attribute(k, v);
        });
        sparkSession.attributes().forEach(e -> { // Removed attributes
            if (!session.getAttributes().keySet().contains(e)) {
                sparkSession.removeAttribute(e);
            }
        });
        if (session.invalid()) {
            sparkSession.invalidate();
        }
    }

}

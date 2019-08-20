package m.core.common;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.impl.client.BasicCookieStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.Result;

import m.core.http.Request;
import m.core.http.Response;
import m.core.http.Session;

/**
 * A helper utility for debugging common HTTP objects.
 * <p>
 * Can print to console or log using the configured {@link Logger}.
 *
 * Can be turned of/off using {@link Debug#enable()}/{@link Debug#disable()}.
 */
public class Debug {

    private static boolean enabled = true;
    private static Logger logger = LoggerFactory.getLogger(Debug.class);

    /**
     * Disables both logging and console output of this debug helper utility.
     * <p>
     * Both logging and console output are enabled by default.
     */
    public static void disable() {
        Debug.enabled = false;
    }

    /**
     * Enables both logging and console output of this debug helper utility.
     * <p>
     * Both logging and console output are enabled by default. Use this if it was
     * disabled using {@link Debug#disable()}
     */
    public static void enable() {
        Debug.enabled = true;
    }

    /**
     * Checks if logging and console output of this debug helper utility is enabled.
     *
     * @return true if enables, false otherwise
     */
    public static boolean isEnabled() {
        return enabled;
    }

    /**
     * Logs the content of the specified cookie store.
     *
     * @param cookieStore the cookie store
     */
    public static void log(BasicCookieStore cookieStore) {
        printOrLog(cookieStore, false);
    }

    private static void log(boolean console) {
        if (!enabled) {
            return;
        }
        if (console) {
            System.out.println();
        } else {
            logger.info("");
        }
    }

    /**
     * Logs the specified cookie.
     *
     * @param cookie the cookie
     */
    public static void log(Cookie cookie) {
        printOrLog(cookie, false);
    }

    /**
     * Logs the specified HTTP servlet request.
     *
     * @param request the HTTP servlet request
     */
    public static void log(HttpServletRequest request) {
        printOrLog(request, false);
    }

    /**
     * Logs the specified list of Apache HTTP cookies.
     *
     * @param cookies the list of cookies
     */
    public static void log(List<org.apache.http.cookie.Cookie> cookies) {
        printOrLog(cookies, false);
    }

    /**
     * Logs the specified cookie from Apache HTTP libraray.
     *
     * @param cookie the cookie
     */
    public static void log(org.apache.http.cookie.Cookie cookie) {
        printOrLog(cookie, false);
    }

    /**
     * Logs the specified cookie from Play framework.
     *
     * @param cookie the cookie
     */
    public static void log(play.mvc.Http.Cookie cookie) {
        printOrLog(cookie, false);
    }

    /**
     * Logs the specified HTTP request.
     *
     * @param request the request
     */
    public static void log(Request request) {
        printOrLog(request, false);
    }

    /**
     * Logs the specified HTTP response.
     *
     * @param response the response
     */
    public static void log(Response response) {
        printOrLog(response, false);
    }

    /**
     * Logs the specified action result from Play framework.
     *
     * @param result the action result.
     */
    public static void log(Result result) {
        printOrLog(result, false);

    }

    /**
     * Logs the specified HTTP session.
     *
     * @param session the session
     */
    public static void log(Session session) {
        printOrLog(session, false);

    }

    /**
     * Logs the specified HTTP request from Spark java framework.
     *
     * @param request the request
     */
    public static void log(spark.Request request) {
        printOrLog(request, false);
    }

    private static void log(String line, boolean console) {
        if (!enabled) {
            return;
        }
        if (console) {
            System.out.println(line);
        } else {
            logger.info(line);
        }
    }

    /**
     * Prints the content of the specified cookie store.
     *
     * @param cookieStore the cookie store
     */
    public static void print(BasicCookieStore cookieStore) {
        printOrLog(cookieStore, true);
    }

    /**
     * Prints the specified cookie.
     *
     * @param cookie the cookie
     */
    public static void print(Cookie cookie) {
        printOrLog(cookie, true);
    }

    /**
     * Prints the specified HTTP servlet request.
     *
     * @param request the HTTP servlet request
     */
    public static void print(HttpServletRequest request) {
        printOrLog(request, true);
    }

    /**
     * Prints the specified list of Apache HTTP cookies.
     *
     * @param cookies the list of cookies
     */
    public static void print(List<org.apache.http.cookie.Cookie> cookies) {
        printOrLog(cookies, true);
    }

    /**
     * Prints the specified cookie from Apache HTTP library.
     *
     * @param cookie the cookie
     */
    public static void print(org.apache.http.cookie.Cookie cookie) {
        printOrLog(cookie, true);
    }

    /**
     * Prints the specified cookie from Play framework.
     *
     * @param cookie the cookie
     */
    public static void print(play.mvc.Http.Cookie cookie) {
        printOrLog(cookie, true);
    }

    /**
     * Prints the specified HTTP request.
     *
     * @param request the request
     */
    public static void print(Request request) {
        printOrLog(request, true);
    }

    /**
     * Prints the specified HTTP response.
     *
     * @param response the response
     */
    public static void print(Response response) {
        printOrLog(response, true);
    }

    /**
     * Prints the specified action result from Play framework.
     *
     * @param result the action result.
     */
    public static void print(Result result) {
        printOrLog(result, true);
    }

    /**
     * Prints the specified HTTP session.
     *
     * @param session the session
     */
    public static void print(Session session) {
        printOrLog(session, true);
    }

    /**
     * Prints the specified HTTP request from Spark java framework.
     *
     * @param request the request
     */
    public static void print(spark.Request request) {
        printOrLog(request, true);
    }

    private static void printOrLog(BasicCookieStore cookieStore, boolean console) {
        log("[COOKIE STORE", console);
        cookieStore.getCookies().forEach(e -> {
            printOrLog(e, console);
        });
        log("COOKIE STORE]", console);
    }

    private static void printOrLog(Cookie cookie, boolean console) {
        if (cookie == null) {
            log("COOKIE: NULL", console);
            return;
        }
        log("COOKIE", console);
        log(" NAME: " + cookie.getName(), console);
        log(" VALUE: " + cookie.getValue(), console);
        log(" PATH: " + cookie.getPath(), console);
        log(" DOMAIN: " + cookie.getDomain(), console);
        log(" MAXAGE: " + cookie.getMaxAge(), console);
        log(" VERSION: " + cookie.getVersion(), console);
        log(" COMMENT: " + cookie.getComment(), console);
    }

    private static void printOrLog(HttpServletRequest request, boolean console) {
        log(console);
        log("HTTPSERVLETREQUEST]", console);
        log("HEADERS", console);
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            Enumeration<String> values = request.getHeaders(header);
            while (values.hasMoreElements()) {
                log(" " + header + ":" + values.nextElement(), console);
            }
        }
        log("HTTPSERVLETREQUEST]", console);
        log(console);
    }

    private static void printOrLog(List<org.apache.http.cookie.Cookie> cookies, boolean console) {
        log("[STORE", console);
        cookies.forEach(e -> {
            printOrLog(e, console);
        });
        log("STORE]", console);
    }

    private static void printOrLog(org.apache.http.cookie.Cookie cookie, boolean console) {
        log("NAME: " + cookie.getName(), console);
        log(" VALUE: " + cookie.getValue(), console);
        log(" PATH: " + cookie.getPath(), console);
        log(" DOMAIN: " + cookie.getDomain(), console);
        log(" EXPIRYDATE: " + cookie.getExpiryDate(), console);
        log(" VERSION: " + cookie.getVersion(), console);
        log(" SECURE: " + cookie.isSecure(), console);
        log(" PERSISTENT: " + cookie.isPersistent(), console);
        log(" COMMENT: " + cookie.getComment(), console);
        log(" COMMENTURL: " + cookie.getCommentURL(), console);
    }

    private static void printOrLog(play.mvc.Http.Cookie cookie, boolean console) {
        log("NAME: " + cookie.name(), console);
        log(" VALUE: " + cookie.value(), console);
        log(" PATH: " + cookie.path(), console);
        log(" DOMAIN: " + cookie.domain(), console);
        log(" MAXAGE: " + cookie.maxAge(), console);
        log(" SECURE: " + cookie.secure(), console);
        log(" HTTPONLY: " + cookie.httpOnly(), console);
        log(" SAMESITE: " + cookie.sameSite(), console);
    }

    private static void printOrLog(Request request, boolean console) {
        if (request == null) {
            log("REQUEST: NULL", console);
            return;
        }
        log(console);
        log("[REQUEST", console);
        log("BODY", console);
        log(" " + request.getBody(), console);
        log("PATH PARAMS", console);
        request.getPathParams().forEach((k, v) -> {
            log(" " + k + ": " + v, console);
        });
        log("QUERY PARAMS", console);
        request.getQueryParams().forEach((k, v) -> {
            log(" " + k + ": " + v, console);
        });
        log("HEADERS", console);
        request.getHeaders().forEach((k, v) -> {
            log(" " + k + ":" + v, console);
        });
        request.getCookies().forEach((k, v) -> {
            v.forEach(e -> {
                printOrLog(e, console);
            });
        });
        printOrLog(request.getSession(), console);
        log("REQUEST]", console);
        log(console);
    }

    private static void printOrLog(Response response, boolean console) {
        if (response == null) {
            log("RESPONSE: NULL", console);
            return;
        }
        log(console);
        log("[RESPONSE", console);
        log("BODY", console);
        log(" " + response.getBody(), console);
        log("STATUS: " + response.getStatus(), console);
        log("HEADERS", console);
        response.getHeaders().forEach((k, v) -> {
            log(" " + k + ":" + v, console);
        });
        response.getCookies().forEach((k, v) -> {
            v.forEach(e -> {
                printOrLog(e, console);
            });
        });
        log("RESPONSE]", console);
        log(console);
    }

    private static void printOrLog(Result result, boolean console) {
        if (result == null) {
            log("PLAY RESPONSE: NULL", console);
            return;
        }
        log(console);
        log("[PLAY RESPONSE", console);
        log("BODY", console);
        log(" " + result.body().as("text/html"), console); // TODO FIXME Content Type
        log("STATUS: " + result.status(), console);
        log("HEADERS", console);
        result.headers().forEach((k, v) -> {
            log(" " + k + ":" + v, console);
        });
        log("COOKIES", console);
        result.cookies().forEach(e -> {
            printOrLog(e, console);
        });
        log("PLAY RESPONSE]", console);
        log(console);

    }

    private static void printOrLog(Session session, boolean console) {
        if (session == null) {
            log("SESSION: NULL", console);
            return;
        }
        log("SESSION", console);
        log(" ID: " + session.getId(), console);
        log(" LAST ACCESS: " + session.getLastAccessedTime(), console);
        log(" INACTIVE TIMEOUT: " + session.getMaxInactiveInterval(), console);
        log(" VALID: " + session.valid(), console);
        session.getAttributes().forEach((k, v) -> {
            log("  " + k + ": " + v.toString(), console);
        });
    }

    private static void printOrLog(spark.Request request, boolean console) {
        if (request == null) {
            log("SPARK REQUEST: NULL", console);
            return;
        }
        log(console);
        log("[SPARK REQUEST", console);
        log("BODY", console);
        log(" " + request.body(), console);
        log("PATH PARAMS", console);
        request.params().forEach((k, v) -> {
            log(" " + k + ": " + v, console);
        });
        log("QUERY PARAMS", console);
        request.queryMap().toMap().forEach((k, v) -> {
            Arrays.asList(v).forEach(e -> {
                log(" " + k + ": " + e, console);
            });
        });
        log("HEADERS", console);
        request.headers().forEach(k -> {
            log(" " + k + ":" + request.headers(k), console);
        });
        print(request.raw());
        request.cookies().forEach((k, v) -> {
            log(" COOKIE " + k + ": " + v, console);
        });
        if (request.session() != null) {
            log("SESSION", console);
            log(" ID: " + request.session().id(), console);
            request.session().attributes().forEach(e -> {
                log(" " + e + "=" + request.session().attribute(e), console);
            });
        }
        log("SPARK REQUEST]", console);
        log(console);

    }

    // no instantiations
    private Debug() {

    }

}

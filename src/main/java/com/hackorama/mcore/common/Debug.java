package com.hackorama.mcore.common;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.impl.client.BasicCookieStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Debug {

    private static boolean enabled = true;
    private static Logger logger = LoggerFactory.getLogger(Debug.class);

    public static void disable() {
        Debug.enabled = false;
    }

    public static void enable() {
        Debug.enabled = true;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void log(BasicCookieStore cookieStore) {
        printOrLog(cookieStore, false);
    }

    public static void log(boolean console) {
        if (!enabled) {
            return;
        }
        if (console) {
            System.out.println();
        } else {
            logger.info("");
        }
    }

    public static void log(Cookie cookie) {
        printOrLog(cookie, false);
    }

    public static void log(HttpServletRequest request) {
        printOrLog(request, false);
    }

    public static void log(List<org.apache.http.cookie.Cookie> cookies) {
        printOrLog(cookies, false);
    }

    public static void log(org.apache.http.cookie.Cookie cookie) {
        printOrLog(cookie, false);
    }

    public static void log(Request request) {
        printOrLog(request, false);
    }

    public static void log(Response response) {
        printOrLog(response, false);
    }

    public static void log(Session session) {
        printOrLog(session, false);

    }

    public static void log(spark.Request request) {
        printOrLog(request, false);
    }

    public static void log(String line, boolean console) {
        if (!enabled) {
            return;
        }
        if (console) {
            System.out.println(line);
        } else {
            logger.info(line);
        }
    }

    public static void print(BasicCookieStore cookieStore) {
        printOrLog(cookieStore, true);
    }

    public static void print(Cookie cookie) {
        printOrLog(cookie, true);
    }

    public static void print(HttpServletRequest request) {
        printOrLog(request, true);
    }

    public static void print(List<org.apache.http.cookie.Cookie> cookies) {
        printOrLog(cookies, true);
    }

    public static void print(org.apache.http.cookie.Cookie cookie) {
        printOrLog(cookie, true);
    }

    public static void print(Request request) {
        printOrLog(request, true);
    }

    public static void print(Response response) {
        printOrLog(response, true);
    }

    public static void print(Session session) {
        printOrLog(session, true);
    }

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
        printOrLog(response.getSession(), console);
        log("RESPONSE]", console);
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
            log(" " + k + ": " + v.toString(), console);
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

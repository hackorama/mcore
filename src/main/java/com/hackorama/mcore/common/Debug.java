package com.hackorama.mcore.common;

import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.impl.client.BasicCookieStore;

public class Debug {

    public static void print(BasicCookieStore cookieStore) {
        System.out.println("[COOKIE STORE");
        cookieStore.getCookies().forEach(e -> {
            print(e);
        });
        System.out.println("COOKIE STORE]");
    }

    public static void print(Cookie cookie) {
        if (cookie == null) {
            System.out.println("COOKIE: NULL");
            return;
        }
        System.out.println();
        System.out.println("[COOKIE");
        System.out.println(" NAME: " + cookie.getName());
        System.out.println(" VALUE: " + cookie.getValue());
        System.out.println(" PATH: " + cookie.getPath());
        System.out.println(" DOMAIN: " + cookie.getDomain());
        System.out.println(" MAXAGE: " + cookie.getMaxAge());
        System.out.println(" VERSION: " + cookie.getVersion());
        System.out.println(" COMMENT: " + cookie.getComment());
        System.out.println("COOKIE]");
        System.out.println();
    }

    private static void print(HttpServletRequest request) {
        System.out.println();
        System.out.println("HTTPSERVLETREQUEST]");
        System.out.println("HEADERS");
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            Enumeration<String> values = request.getHeaders(header);
            while (values.hasMoreElements()) {
                System.out.println(" " + header + ":" + values.nextElement());
            }
        }
        System.out.println("HTTPSERVLETREQUEST]");
        System.out.println();
    }

    public static void print(org.apache.http.cookie.Cookie cookie) {
        System.out.println("NAME: " + cookie.getName());
        System.out.println(" VALUE: " + cookie.getValue());
        System.out.println(" PATH: " + cookie.getPath());
        System.out.println(" DOMAIN: " + cookie.getDomain());
        System.out.println(" EXPIRYDATE: " + cookie.getExpiryDate());
        System.out.println(" VERSION: " + cookie.getVersion());
        System.out.println(" SECURE: " + cookie.isSecure());
        System.out.println(" PERSISTENT: " + cookie.isPersistent());
        System.out.println(" COMMENT: " + cookie.getComment());
        System.out.println(" COMMENTURL: " + cookie.getCommentURL());
    }

    public static void print(Request request) {
        if (request == null) {
            System.out.println("REQUEST: NULL");
            return;
        }
        System.out.println();
        System.out.println("[REQUEST");
        System.out.println("BODY");
        System.out.println(" " + request.getBody());
        System.out.println("PATH PARAMS");
        request.getPathParams().forEach((k, v) -> {
            System.out.println(" " + k + ": " + v);
        });
        System.out.println("QUERY PARAMS");
        request.getQueryParams().forEach((k, v) -> {
            System.out.println(" " + k + ": " + v);
        });
        System.out.println("HEADERS");
        request.getHeaders().forEach((k, v) -> {
            System.out.println(" " + k + ":" + v);
        });
        System.out.println("COOKIES");
        request.getCookies().forEach((k, v) -> {
            v.forEach(e -> {
                System.out.println("COOKIE " + k);
                print(e);
            });
        });
        System.out.println("REQUEST]");
        System.out.println();
    }

    public static void print(Response response) {
        if (response == null) {
            System.out.println("RESPONSE: NULL");
            return;
        }
        System.out.println();
        System.out.println("[RESPONSE");
        System.out.println("BODY");
        System.out.println(" " + response.getBody());
        System.out.println("STATUS: " + response.getStatus());
        System.out.println("HEADERS");
        response.getHeaders().forEach((k, v) -> {
            System.out.println(" " + k + ":" + v);
        });
        System.out.println("COOKIES");
        response.getCookies().forEach((k, v) -> {
            v.forEach(e -> {
                System.out.println("COOKIE " + k);
                print(e);
            });
        });
        System.out.println("RESPONSE]");
        System.out.println();
    }

    public static void print(spark.Request request) {
        if (request == null) {
            System.out.println("SPAEK REQUEST: NULL");
            return;
        }
        System.out.println();
        System.out.println("[SPARK REQUEST");
        System.out.println("BODY");
        System.out.println(" " + request.body());
        System.out.println("PATH PARAMS");
        request.params().forEach((k, v) -> {
            System.out.println(" " + k + ": " + v);
        });
        System.out.println("QUERY PARAMS");
        request.queryMap().toMap().forEach((k, v) -> {
            Arrays.asList(v).forEach(e -> {
                System.out.println(" " + k + ": " + e);
            });
        });
        System.out.println("HEADERS");
        request.headers().forEach(k -> {
            System.out.println(" " + k + ":" + request.headers(k));
        });
        print(request.raw());
        System.out.println("COOKIES");
        request.cookies().forEach((k, v) -> {
            System.out.println("COOKIE " + k + ": " + v);
        });
        System.out.println("SPARK REQUEST]");
        System.out.println();

    }

    // no instantiations
    private Debug() {

    }

}

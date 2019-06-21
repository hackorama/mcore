package com.hackorama.mcore.common;

import javax.servlet.http.Cookie;

public class Debug {

    public static void print(Response response) {
        if (response == null) {
            System.out.println("RESPONSE: NULL");
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
            System.out.println("COOKIE " + k);
            print(v);
        });
        System.out.println("RESPONSE]");
        System.out.println();
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
            System.out.println("COOKIE " + k);
            print(v);
        });
        System.out.println("REQUEST]");
        System.out.println();
    }

    // no instantiations
    private Debug() {

    }

}

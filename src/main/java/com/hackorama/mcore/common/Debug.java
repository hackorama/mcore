package com.hackorama.mcore.common;

public class Debug {

    public static void request(Request request) {
        if (request == null) {
            System.out.println("REQUEST: NULL");
            return;
        }
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
    }

    // no instantiations
    private Debug() {

    }

}

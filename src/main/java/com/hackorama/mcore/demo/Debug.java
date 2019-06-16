package com.hackorama.mcore.demo;

import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.SuppressFBWarnings;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.service.BaseService;

public class Debug {

    public static void main(String[] args) {
        new BaseService() {

            @Override
            public void configure() {
                GET("/test", this::test);
                GET("/test/", this::test);
                GET("/test/{test}", this::test);
                GET("/test/{test}/", this::test);
                POST("/test", this::test);
                POST("/test/", this::test);
                POST("/test/{test}", this::test);
                POST("/test/{test}/", this::test);
            }

            private void debug(Request request) {
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

            @SuppressFBWarnings // Ignore invalid UMAC warning, method is accessed by Function interface
            public Response test(Request request) {
                debug(request);
                return new Response("DEBUG");
            }

        }.configureUsing(new SparkServer("Debug")).start();
    }
}

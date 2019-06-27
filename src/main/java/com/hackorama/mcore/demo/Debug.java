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

            @SuppressFBWarnings // Ignore invalid UMAC warning, method is accessed by Function interface
            public Response test(Request request) {
                com.hackorama.mcore.common.Debug.print(request);
                Response response = new Response("DEBUG");
                com.hackorama.mcore.common.Debug.print(response);
                return response;
            }

        }.configureUsing(new SparkServer("Debug")).start();
    }
}

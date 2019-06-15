package com.hackorama.mcore.demo;

import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.SuppressFBWarnings;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.service.BaseService;

public class Demo {

    public static void main(String[] args) {
        new BaseService() {

            @SuppressFBWarnings // Ignore invalid UMAC warning, method is used by Function interface
            public Response demo(Request request) {
                return new Response("DEMO");
            }

            @Override
            public void configure() {
                GET("/demo", this::demo);
            }

        }.configureUsing(new SparkServer("Demo")).start();
    }
}

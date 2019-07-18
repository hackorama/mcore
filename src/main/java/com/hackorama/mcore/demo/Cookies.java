package com.hackorama.mcore.demo;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;

import com.hackorama.mcore.client.unirest.CookieUnirestClient;
import com.hackorama.mcore.common.Debug;
import com.hackorama.mcore.common.SuppressFBWarnings;
import com.hackorama.mcore.http.Request;
import com.hackorama.mcore.http.Response;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.service.BaseService;
import com.hackorama.mcore.service.Service;

public class Cookies {

    public static void main(String[] args) throws InterruptedException {
        Service service = new BaseService() {

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
                Debug.print(request);
                Response response = new Response("COOKIE TEST");
                Cookie cookie = new Cookie("FUN", "SUMMER");
                cookie.setPath("/test");
                response.setCookie(cookie);
                Debug.print(response);
                return response;
            }

        }.configureUsing(new SparkServer("Debug")).start();

        Thread.sleep(TimeUnit.SECONDS.toMillis(3)); // wait for server to initialize

        System.out.println("Testing cookies ...");

        CookieUnirestClient client = new CookieUnirestClient();
        client.debugPrintCookies();
        client.get("http://localhost:8080/test");
        client.debugPrintCookies();
        client.get("http://localhost:8080/test/cookie");
        client.debugPrintCookies();

        service.stop();
    }
}

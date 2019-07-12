package com.hackorama.mcore.demo;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.client.unirest.CookieUnirestClient;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.SuppressFBWarnings;
import com.hackorama.mcore.common.Util;
import com.hackorama.mcore.server.Server;
import com.hackorama.mcore.server.play.PlayServer;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.server.spring.SpringServer;
import com.hackorama.mcore.server.vertx.VertxServer;
import com.hackorama.mcore.service.BaseService;
import com.hackorama.mcore.service.Service;

public class Servers {

    private static class Result {
        List<org.apache.http.cookie.Cookie> cookieResults = new ArrayList<>();
        SortedMap<String, List<String>> headerResults = new TreeMap<>();
    }

    public static void main(String[] args) throws InterruptedException, UnirestException {
        Map<String, Result> results = new TreeMap<>();
        results.put("SPARK", runOnServer(new SparkServer("Spark")));
        results.put("SPRING", runOnServer(new SpringServer("Spring")));
        results.put("VERTX", runOnServer(new VertxServer("Vertx")));
        results.put("PLAY", runOnServer(new PlayServer("Play")));
        System.out.println("COOKIES");
        results.forEach((k, v) -> {
            System.out.println("  " + k);
            v.cookieResults.forEach(e -> {
                if (e.getName().startsWith("REQUEST_") || e.getName().startsWith("RESPONSE_")) {
                    System.out.println("    " + e.getName() + " = " + e.getValue());
                }
            });
            System.out.println();
        });
        System.out.println("HEADERS");
        results.forEach((k, v) -> {
            System.out.println("  " + k);
            v.headerResults.forEach((hk, hv) -> {
                if (hk.startsWith("REQUEST_") || hk.startsWith("RESPONSE_")) {
                    hv.forEach(e -> {
                        System.out.println("    " + hk + " = " + e);
                    });
                }
            });
            System.out.println();
        });
    }

    public static Result runOnServer(Server server) throws InterruptedException, UnirestException {

        System.out.println("Running on " + server.getName() + " server ...");

        Service service = new BaseService() {

            @Override
            public void configure() {
                GET("/cookie", this::cookie);
                GET("/header", this::header);
            }

            @SuppressFBWarnings // Ignore invalid UMAC warning, method is accessed by Function interface
            public Response cookie(Request request) {
                Response response = new Response("COOKIES");
                Cookie cookie = new Cookie("RESPONSE_COOKIE_SINGLE_VALUE", "ONE");
                response.setCookie(cookie);
                cookie = new Cookie("RESPONSE_COOKIE_MULTI_VALUE", "FIRST");
                cookie.setPath("/first");
                response.setCookie(cookie);
                cookie = new Cookie("RESPONSE_COOKIE_MULTI_VALUE", "SECOND");
                cookie.setPath("/second");
                cookie = new Cookie("RESPONSE_COOKIE_MULTI_VALUE", "LAST");
                cookie.setPath("/last");
                response.setCookie(cookie);
                request.getCookies().forEach((k, v) -> {
                    v.forEach(response::setCookie);
                });
                return response;
            }

            @SuppressFBWarnings // Ignore invalid UMAC warning, method is accessed by Function interface
            public Response header(Request request) {
                Response response = new Response(Util.getGson().toJson(request.getHeaders()));
                Map<String, List<String>> headers = new HashMap<>();
                headers.put("RESPONSE_HEADER_ONE", Stream.of("ONE").collect(Collectors.toList()));
                headers.put("RESPONSE_HEADER_MANY", Stream.of("FIRST", "SECOND", "LAST").collect(Collectors.toList()));
                headers.put("RESPONSE_HEADER_DUPLICATE", Stream.of("SAME", "SAME").collect(Collectors.toList()));
                response.setHeaders(headers);
                return response;
            }

        }.configureUsing(server).start();

        Thread.sleep(TimeUnit.SECONDS.toMillis(3)); // Wait for server to initialize

        Result result = new Result();
        CookieUnirestClient client = new CookieUnirestClient(); // Sets up cookie jar
        client.clearCookies();
        if (Unirest.get("http://localhost:8080/cookie").header("Cookie",
                "REQUEST_COOKIE_SINGLE_VALUE=ONE;REQUEST_COOKIE_MULTI_VALUE=FIRST;REQUEST_COOKIE_MULTI_VALUE=SECOND;REQUEST_COOKIE_MULTI_VALUE=LAST;REQUEST_COOKIE_DUPLICATE=SAME;REQUEST_COOKIE_DUPLICATE=SAME")
                .asString().getStatus() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed processing cookies correctly");
        }
        result.cookieResults = client.getCookies();
        HttpResponse<JsonNode> response = Unirest.get("http://localhost:8080/header").header("REQUEST_ONLY", "ONE")
                .header("REQUEST_MANY", "FIRST").header("REQUEST_MANY", "SECOND").header("REQUEST_MANY", "LAST")
                .header("REQUEST_DUPLICATE", "SAME").header("REQUEST_DUPLICATE", "SAME").asJson();
        result.headerResults.putAll(response.getHeaders());
        response.getBody().getObject().keySet().forEach(k -> {
            result.headerResults.put(k, new ArrayList<>());
            response.getBody().getObject().getJSONArray(k).forEach(e -> {
                result.headerResults.get(k).add((String) e);
            });
        });
        service.stop();
        return result;
    }
}

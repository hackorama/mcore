package m.core.server;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

import m.core.client.unirest.CookieUnirestClient;
import m.core.common.Util;
import m.core.http.Request;
import m.core.http.Response;
import m.core.server.play.PlayServer;
import m.core.server.spark.SparkServer;
import m.core.server.spring.SpringServer;
import m.core.server.vertx.VertxServer;
import m.core.service.Service;

public class Servers {

    private static class Result {
        List<org.apache.http.cookie.Cookie> cookies = new ArrayList<>();
        SortedMap<String, List<String>> headers = new TreeMap<>();
    }

    public static void addColumnCell(SortedMap<String, List<String>> table, int column, String name, String value) {
        if (!table.containsKey(name)) {
            table.put(name, new ArrayList<>());
        }
        List<String> row = table.get(name);
        if (row.size() < column) {
            row.add("");
        } else if (row.size() == column) {
            row.add(value);
        } else if (row.size() > column) {
            row.set(row.size() - 1, row.get(row.size() - 1) + " " + value);
        }
    }

    public static void main(String[] args) throws InterruptedException, UnirestException {
        Map<String, Result> results = new TreeMap<>();
        results.put("SPARK", runOnServer(new SparkServer("Spark")));
        results.put("SPRING", runOnServer(new SpringServer("Spring")));
        results.put("VERTX", runOnServer(new VertxServer("Vertx")));
        results.put("PLAY", runOnServer(new PlayServer("Play")));

        SortedMap<String, List<String>> cookieTable = new TreeMap<>();
        int column = 0;
        for (Entry<String, Result> entry : results.entrySet()) {
            addColumnCell(cookieTable, column, "COOKIE", entry.getKey());
            for (org.apache.http.cookie.Cookie cookie : entry.getValue().cookies) {
                if (cookie.getName().startsWith("REQUEST_") || cookie.getName().startsWith("RESPONSE_")) {
                    addColumnCell(cookieTable, column, cookie.getName(), cookie.getValue());
                }
            }
            column++;
        }
        SortedMap<String, List<String>> headerTable = new TreeMap<>();
        column = 0;
        for (Entry<String, Result> entry : results.entrySet()) {
            addColumnCell(headerTable, column, "HEADER", entry.getKey());
            for (Entry<String, List<String>> header : entry.getValue().headers.entrySet()) {
                if (header.getKey().startsWith("REQUEST_") || header.getKey().startsWith("RESPONSE_")) {
                    for (String value : header.getValue()) {
                        addColumnCell(headerTable, column, header.getKey(), value);
                    }
                }
            }
            column++;
        }

        print(cookieTable);
        System.out.println();
        print(headerTable);
    }

    public static void print(SortedMap<String, List<String>> table) {
        System.out.println();
        table.forEach((k, v) -> {
            System.out.format("| %-28s |", k);
            v.forEach(e -> {
                System.out.format(" %-18s |", e);
            });
            System.out.println();
        });
    }

    public static Result runOnServer(Server server) throws InterruptedException, UnirestException {

        System.out.println("Running on " + server.getName() + " server ...");

        Service service = new Service() {

            @Override
            public void configure() {
                GET("/cookie", this::cookie);
                GET("/header", this::header);
            }

            public Response cookie(Request request) {
                Response response = new Response("COOKIES");
                Cookie cookie = new Cookie("RESPONSE_COOKIE_SINGLE_VALUE", "ONE");
                response.setCookie(cookie);
                cookie = new Cookie("RESPONSE_COOKIE_MULTI_VALUE", "FIRST");
                cookie.setPath("/first");
                response.setCookie(cookie);
                cookie = new Cookie("RESPONSE_COOKIE_MULTI_VALUE", "SECOND");
                cookie.setPath("/second");
                response.setCookie(cookie);
                cookie = new Cookie("RESPONSE_COOKIE_MULTI_VALUE", "LAST");
                cookie.setPath("/last");
                response.setCookie(cookie);
                request.getCookies().forEach((k, v) -> {
                    v.forEach(response::setCookie);
                });
                return response;
            }

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
        result.cookies = client.getCookies();
        HttpResponse<JsonNode> response = Unirest.get("http://localhost:8080/header").header("REQUEST_ONLY", "ONE")
                .header("REQUEST_MANY", "FIRST").header("REQUEST_MANY", "SECOND").header("REQUEST_MANY", "LAST")
                .header("REQUEST_DUPLICATE", "SAME").header("REQUEST_DUPLICATE", "SAME").asJson();
        result.headers.putAll(response.getHeaders());
        response.getBody().getObject().keySet().forEach(k -> {
            result.headers.put(k, new ArrayList<>());
            response.getBody().getObject().getJSONArray(k).forEach(e -> {
                result.headers.get(k).add((String) e);
            });
        });
        service.stop();
        return result;
    }
}

package m.core.samples;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import m.core.common.Util;
import m.core.data.MemoryDataStore;
import m.core.http.Request;
import m.core.http.Response;
import m.core.server.spark.SparkServer;
import m.core.service.Service;

public class UserService extends Service {

    class User {

        private String email;
        private String id;
        private String name;

        public String getEmail() {
            return email;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setId() {
            id = java.util.UUID.randomUUID().toString();
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private static final Gson GSON = new Gson();
    private static final String STORE_NAME = "user";

    public static Response headersUser(Request request) {
        Response response = new Response();
        Map<String, List<String>> headers = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("OK");
        headers.put("HEAD", values);
        response.setHeaders(headers);
        return response;
    }

    public static void main(String[] args) throws UnirestException, InterruptedException {
        Service userService = new UserService().configureUsing(new SparkServer("User Service"))
                .configureUsing(new MemoryDataStore()).start();

        Thread.sleep(TimeUnit.SECONDS.toMillis(3)); // wait for server to initialize

        System.out.println("Testing user service ...");

        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post("http://127.0.0.1:8080/user").header("accept", "application/json")
                .body("{ \"name\" : \"one\", \"email\" : \"one@example.com\" }").asJson();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(jsonResponse.getBody().getObject()));
        userService.stop();

    }

    private static Response optionsUser(Request request) {
        Response response = new Response();
        Map<String, List<String>> headers = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS");
        headers.put("Allow", values);
        response.setHeaders(headers);
        return response;
    }

    private static Response traceUser(Request request) {
        Response response = new Response();
        response.setBody(request.getBody());
        return response;
    }

    @Override
    public void configure() {
        GET("/user", this::getUser);
        GET("/user/{id}", this::getUser);
        POST("/user", this::createUser);
        PUT("/user/{id}", this::editUser);
        DELETE("/user/{id}", this::deleteUser);
        PATCH("/user/{id}", this::editUserProperties);
        OPTIONS("/user", UserService::optionsUser);
        HEAD("/user", UserService::headersUser);
        TRACE("/user", UserService::traceUser);
    }

    private Response createUser(Request request) {
        return editUser(request);
    }

    private Response deleteUser(Request request) {
        dataStore.remove(STORE_NAME, request.getPathParams().get("id"));
        return new Response("");
    }

    private Response editUser(Request request) {
        User user = GSON.fromJson(request.getBody(), User.class);
        String id = request.getPathParams().get("id");
        if (id != null) { // updating existing
            user.setId(id);
        } else { // adding as new
            user.setId();
        }
        dataStore.put(STORE_NAME, user.getId(), GSON.toJson(user));
        return new Response(GSON.toJson(user));
    }

    private Response editUserProperties(Request request) {
        User updatedUser = GSON.fromJson(request.getBody(), User.class);
        String id = request.getPathParams().get("id");
        if (dataStore.contains(STORE_NAME, id)) {
            User existingUser = GSON.fromJson(dataStore.get(STORE_NAME, id), User.class);
            if (updatedUser.getName() != null) {
                existingUser.setName(updatedUser.getName());
            }
            if (updatedUser.getEmail() != null) {
                existingUser.setEmail(updatedUser.getEmail());
            }
            dataStore.put(STORE_NAME, existingUser.getId(), GSON.toJson(existingUser));
            return new Response(GSON.toJson(existingUser));
        } else {
            return new Response(Util.toJsonString("error", "Invalid User"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private Response getUser(Request request) {
        String id = request.getPathParams().get("id");
        if (id == null) {
            List<User> users = new ArrayList<>();
            for (String data : dataStore.get(STORE_NAME)) {
                users.add(GSON.fromJson(data, User.class));
            }
            return new Response(GSON.toJson(users));
        } else if (dataStore.contains(STORE_NAME, id)) {
            return new Response(dataStore.get(STORE_NAME, id));
        }
        return new Response(Util.toJsonString("error", "Invalid User"), HttpURLConnection.HTTP_BAD_REQUEST);
    }

}

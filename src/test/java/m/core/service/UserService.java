package m.core.service;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import m.core.common.Util;
import m.core.http.Request;
import m.core.http.Response;

public class UserService extends Service {

    private static final Gson GSON = new Gson();
    private static final String STORE_NAME = "user";

    public Response createUser(Request request) {
        return editUser(request);
    }

    public Response deleteUser(Request request) {
        dataStore.remove(STORE_NAME, request.getPathParams().get("id"));
        return new Response("");
    }

    public Response editUser(Request request) {
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

    public Response editUserProperties(Request request) {
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

    public Response getUser(Request request) {
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

    public static Response headersUser(Request request) {
        Response response = new Response();
        Map<String, List<String>> headers = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("OK");
        headers.put("HEAD", values);
        response.setHeaders(headers);
        return response;
    }

    public static Response optionsUser(Request request) {
        Response response = new Response();
        Map<String, List<String>> headers = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS");
        headers.put("Allow", values);
        response.setHeaders(headers);
        return response;
    }

    public static Response traceUser(Request request) {
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

}

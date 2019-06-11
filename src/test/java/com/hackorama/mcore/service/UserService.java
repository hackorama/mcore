package com.hackorama.mcore.service;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.Util;

public class UserService extends BaseService {

    private static final String STORE_NAME = "user";
    private static final Gson GSON = new Gson();

    public static Response createUser(Request request) {
        return editUser(request);
    }

    public static Response deleteUser(Request request) {
        dataStore.remove(STORE_NAME, request.getParams().get("id"));
        return new Response("");
    }

    public static Response editUser(Request request) {
        User user = GSON.fromJson(request.getBody(), User.class);
        String id = request.getParams().get("id");
        if (id != null) { // updating existing
            user.setId(id);
        } else { // adding as new
            user.setId();
        }
        dataStore.put(STORE_NAME, user.getId(), GSON.toJson(user));
        return new Response(GSON.toJson(user));
    }

    public static Response getUser(Request request) {
        String id = request.getParams().get("id");
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

    @Override
    public void configure() {
        server.setRoutes(HttpMethod.GET, "/user", UserService::getUser);
        server.setRoutes(HttpMethod.GET, "/user/{id}", UserService::getUser);
        server.setRoutes(HttpMethod.POST, "/user", UserService::createUser);
        server.setRoutes(HttpMethod.PUT, "/user/{id}", UserService::editUser);
        server.setRoutes(HttpMethod.DELETE, "/user/{id}", UserService::deleteUser);
    }

}

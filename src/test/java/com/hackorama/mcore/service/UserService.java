package com.hackorama.mcore.service;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

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
        dataStore.remove(STORE_NAME, request.getPathParams().get("id"));
        return new Response("");
    }

    public static Response editUser(Request request) {
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

    public static Response getUser(Request request) {
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

    @Override
    public void configure() {
        GET("/user", UserService::getUser);
        GET("/user/{id}", UserService::getUser);
        POST("/user", UserService::createUser);
        PUT("/user/{id}", UserService::editUser);
        DELETE("/user/{id}", UserService::deleteUser);
    }

}

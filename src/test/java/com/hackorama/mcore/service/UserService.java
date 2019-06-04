package com.hackorama.mcore.service;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.Util;
import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.data.MemoryDataStore;
import com.hackorama.mcore.data.cache.DataCache;
import com.hackorama.mcore.data.queue.DataQueue;
import com.hackorama.mcore.server.Server;

public class UserService implements Service {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    private static final String STORE_NAME = "user";
    private static final Gson GSON = new Gson();
    private static DataStore dataStore = new MemoryDataStore();
    private static DataCache dataCache;
    private static DataQueue dataQueue;
    private static Server server;

    @Override
    public Service attach(Service service) {
        service.configureUsing(server);
        return this;
    }

    public static Response createUser(Request request) {
        return editUser(request);
    }

    public static Response deleteUser(Request request) {
        dataStore.remove(STORE_NAME, request.getParams().get("id"));
        return new Response("");
    }

    public static Response editUser(Request request) {
        Gson gson = GSON;
        User user = gson.fromJson(request.getBody(), User.class);
        String id = request.getParams().get("id");
        if (id != null) { // updating existing
            user.setId(id);
        } else { // adding as new
            user.setId();
        }
        dataStore.put(STORE_NAME, user.getId(), GSON.toJson(user));
        return new Response(gson.toJson(user));
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

    private static void setServer(Server server) {
        UserService.server = server;
    }

    private static void setCache(DataCache dataCache) {
        UserService.dataCache = dataCache;
    }

    private static void setQueue(DataQueue dataQueue) {
        UserService.dataQueue = dataQueue;
    }

    private static void setStore(DataStore dataStore) {
        UserService.dataStore = dataStore;
    }

    @Override
    public Service configureUsing(DataCache dataCache) {
        UserService.setCache(dataCache);
        return this;
    }

    @Override
    public Service configureUsing(DataQueue dataQueue) {
        UserService.setQueue(dataQueue);
        return this;
    }

    @Override
    public Service configureUsing(DataStore dataStore) {
        UserService.setStore(dataStore);
        return this;
    }

    @Override
    public UserService configureUsing(Server server) {
        UserService.setServer(server);
        server.setRoutes(HttpMethod.GET, "/user", UserService::getUser);
        server.setRoutes(HttpMethod.GET, "/user/{id}", UserService::getUser);
        server.setRoutes(HttpMethod.POST, "/user", UserService::createUser);
        server.setRoutes(HttpMethod.PUT, "/user/{id}", UserService::editUser);
        server.setRoutes(HttpMethod.DELETE, "/user/{id}", UserService::deleteUser);
        return this;
    }

    @Override
    public Service start() {
        if (server == null) {
            throw new RuntimeException("Please configure a server before starting the servoce");
        }
        logger.info("Starting user service using server {}, data store {}, data cache {}, data queue {}",
                server.getClass().getName(),
                dataStore == null ? "NULL" : dataStore.getClass().getName(),
                dataCache == null ? "NULL" : dataCache.getClass().getName(),
                dataQueue == null ? "NULL" : dataQueue.getClass().getName());
        server.start();
        return this;
    }

    @Override
    public void stop() {
        if(server != null) {
            server.stop();
        }
    }

}

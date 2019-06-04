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

public class GroupService implements Service {

    private static Logger logger = LoggerFactory.getLogger(GroupService.class);

    private static final String STORE_NAME = "group";
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

    public static Response createGroup(Request request) {
        return editGroup(request);
    }

    public static Response deleteGroup(Request request) {
        dataStore.remove(STORE_NAME, request.getParams().get("id"));
        return new Response("");
    }

    public static Response editGroup(Request request) {
        Gson gson = GSON;
        Group group = gson.fromJson(request.getBody(), Group.class);
        String id = request.getParams().get("id");
        if (id != null) { // updating existing
            group.setId(id);
        } else { // adding as new
            group.setId();
        }
        dataStore.put(STORE_NAME, group.getId(), GSON.toJson(group));
        return new Response(gson.toJson(group));
    }

    public static Response getGroup(Request request) {
        String id = request.getParams().get("id");
        if (id == null) {
            List<Group> groups = new ArrayList<>();
            for (String data : dataStore.get(STORE_NAME)) {
                groups.add(GSON.fromJson(data, Group.class));
            }
            return new Response(GSON.toJson(groups));
        } else if (dataStore.contains(STORE_NAME, id)) {
            return new Response(dataStore.get(STORE_NAME, id));
        }
        return new Response(Util.toJsonString("error", "Invalid Group"), HttpURLConnection.HTTP_BAD_REQUEST);
    }

    private static void setServer(Server server) {
        GroupService.server = server;
    }

    private static void setCache(DataCache dataCache) {
        GroupService.dataCache = dataCache;
    }

    private static void setQueue(DataQueue dataQueue) {
        GroupService.dataQueue = dataQueue;
    }

    private static void setStore(DataStore dataStore) {
        GroupService.dataStore = dataStore;
    }

    @Override
    public Service configureUsing(DataCache dataCache) {
        GroupService.setCache(dataCache);
        return this;
    }

    @Override
    public Service configureUsing(DataQueue dataQueue) {
        GroupService.setQueue(dataQueue);
        return this;
    }

    @Override
    public Service configureUsing(DataStore dataStore) {
        GroupService.setStore(dataStore);
        return this;
    }

    @Override
    public GroupService configureUsing(Server server) {
        GroupService.setServer(server);
        server.setRoutes(HttpMethod.GET, "/group", GroupService::getGroup);
        server.setRoutes(HttpMethod.GET, "/group/{id}", GroupService::getGroup);
        server.setRoutes(HttpMethod.POST, "/group", GroupService::createGroup);
        server.setRoutes(HttpMethod.PUT, "/group/{id}", GroupService::editGroup);
        server.setRoutes(HttpMethod.DELETE, "/group/{id}", GroupService::deleteGroup);
        return this;
    }

    @Override
    public Service start() {
        if (server == null) {
            throw new RuntimeException("Please configure a server before starting the servoce");
        }
        logger.info("Starting group service using server {}, data store {}, data cache {}, data queue {}",
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

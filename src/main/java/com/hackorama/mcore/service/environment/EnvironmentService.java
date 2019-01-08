package com.hackorama.mcore.service.environment;


import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.data.MemoryDataStore;
import com.hackorama.mcore.server.Server;
import com.hackorama.mcore.service.Service;

public class EnvironmentService implements Service {

    private static final String STORE_NAME = "ENVIRONMENT";
    private static final Gson GSON = new Gson();
    private static Server server;
    private static DataStore dataStore = new MemoryDataStore();

    public static Response createEnvironment(Request request) {
       return editEnvironment(request);
    }

    public static Response deleteEnvironment(Request request) {
        dataStore.remove(STORE_NAME, request.getParams().get(":id"));
        return new Response("");
    }

    public static Response editEnvironment(Request request) {
        Gson gson = GSON;
        Environment environment = gson.fromJson(request.getBody(), Environment.class);
        String id = request.getParams().get(":id");
        if(id != null) { // updating existing
            environment.setId(id);
        } else { // adding as new
            environment.setId();
        }
        dataStore.put(STORE_NAME, environment.getId(), GSON.toJson(environment));
        return new Response(gson.toJson(environment));
    }

    public static Response getEnvironment(Request request) {
        String id = request.getParams().get(":id");
        if(id != null) {
            return new Response(dataStore.get(STORE_NAME, request.getParams().get(":id")));
        } else {
            List<Environment> environments = new ArrayList<>(); //TODO use gson parsing
            for(String data : dataStore.get(STORE_NAME)) {
                environments.add(GSON.fromJson(data, Environment.class));
            }
            return new Response(GSON.toJson(environments));
        }
    }

    private static void setServer(Server server) {
        EnvironmentService.server = server;
    }

    private static void setStore(DataStore dataStore) {
        EnvironmentService.dataStore = dataStore;
    }

    @Override
    public EnvironmentService configureUsing(DataStore dataStore) {
        EnvironmentService.setStore(dataStore);
        return this;
    }

    @Override
    public EnvironmentService configureUsing(Server server) {
        EnvironmentService.setServer(server);
        try {
            server.setRoutes(HttpMethod.GET, "/environment", EnvironmentService.class.getMethod("getEnvironment", Request.class));
            server.setRoutes(HttpMethod.GET, "/environment/:id", EnvironmentService.class.getMethod("getEnvironment", Request.class));
            server.setRoutes(HttpMethod.POST, "/environment", EnvironmentService.class.getMethod("createEnvironment", Request.class));
            server.setRoutes(HttpMethod.PUT, "/environment/:id", EnvironmentService.class.getMethod("editEnvironment", Request.class));
            server.setRoutes(HttpMethod.DELETE, "/environment/:id", EnvironmentService.class.getMethod("deleteEnvironment", Request.class));
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace(); //TODO implement checked exception
        }
        return this;
    }

    @Override
    public void stop() {
        dataStore.clear(); //TODO FIXME
        server.removeRoutes("environment"); //TOD FIXME
    }

}

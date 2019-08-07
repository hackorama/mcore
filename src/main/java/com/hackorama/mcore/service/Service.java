package com.hackorama.mcore.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.data.MemoryDataStore;
import com.hackorama.mcore.data.cache.DataCache;
import com.hackorama.mcore.data.queue.DataQueue;
import com.hackorama.mcore.http.Method;
import com.hackorama.mcore.http.Request;
import com.hackorama.mcore.http.Response;
import com.hackorama.mcore.server.Server;

public abstract class Service /* implements Service */ {

    private static Logger logger = LoggerFactory.getLogger(Service.class);

    static DataCache dataCache;
    static DataQueue dataQueue;
    static DataStore dataStore = new MemoryDataStore();

    private static void setCache(DataCache dataCache) {
        Service.dataCache = dataCache;
    }

    private static void setQueue(DataQueue dataQueue) {
        Service.dataQueue = dataQueue;
    }

    private static void setStore(DataStore dataStore) {
        Service.dataStore = dataStore;
    }

    private List<Service> attachedServices = new ArrayList<>();

    private Map<Method, Map<String, Function<Request, Response>>> routeHandlerMap = new HashMap<>();

    protected Server server;

    {
        Stream.of(Method.values()).forEach(e -> {
            routeHandlerMap.put(e, new HashMap<>());
        });
    }

    public Service attach(Service service) {
        attachedServices.add(service);
        service.configureUsing(server);
        return this;
    }

    public abstract void configure();

    private void configureRoutes(Server server) {
        configure();
        server.setRoutes(routeHandlerMap);
    }

    public Service configureUsing(DataCache dataCache) {
        Service.setCache(dataCache);
        return this;
    }

    public Service configureUsing(DataQueue dataQueue) {
        Service.setQueue(dataQueue);
        return this;
    }

    public Service configureUsing(DataStore dataStore) {
        Service.setStore(dataStore);
        return this;
    }

    public Service configureUsing(Server server) {
        this.server = server;
        return this;
    }

    public Service DELETE(String path,
            Function<com.hackorama.mcore.http.Request, com.hackorama.mcore.http.Response> handler) {
        ROUTE(Method.DELETE, path, handler);
        return this;
    }

    public Service GET(String path,
            Function<com.hackorama.mcore.http.Request, com.hackorama.mcore.http.Response> handler) {
        ROUTE(Method.GET, path, handler);
        return this;
    }
    public Service POST(String path,
            Function<com.hackorama.mcore.http.Request, com.hackorama.mcore.http.Response> handler) {
        ROUTE(Method.POST, path, handler);
        return this;
    }

    public Service PUT(String path,
            Function<com.hackorama.mcore.http.Request, com.hackorama.mcore.http.Response> handler) {
        ROUTE(Method.PUT, path, handler);
        return this;
    }

    public Service PATCH(String path,
            Function<com.hackorama.mcore.http.Request, com.hackorama.mcore.http.Response> handler) {
        ROUTE(Method.PATCH, path, handler);
        return this;
    }

    public Service OPTIONS(String path,
            Function<com.hackorama.mcore.http.Request, com.hackorama.mcore.http.Response> handler) {
        ROUTE(Method.OPTIONS, path, handler);
        return this;
    }

    public Service TRACE(String path,
            Function<com.hackorama.mcore.http.Request, com.hackorama.mcore.http.Response> handler) {
        ROUTE(Method.TRACE, path, handler);
        return this;
    }

    public Service HEAD(String path,
            Function<com.hackorama.mcore.http.Request, com.hackorama.mcore.http.Response> handler) {
        ROUTE(Method.HEAD, path, handler);
        return this;
    }


    public Service ROUTE(Method method, String path,
            Function<com.hackorama.mcore.http.Request, com.hackorama.mcore.http.Response> handler) {
        routeHandlerMap.get(method).put(path, handler);
        return this;
    }

    public Service start() {
        if (server == null) {
            throw new RuntimeException("Please configure a server before starting the service");
        }
        logger.info("Starting service using server {}, data store {}, data cache {}, data queue {}",
                server.getClass().getName(), dataStore == null ? "NULL" : dataStore.getClass().getName(),
                dataCache == null ? "NULL" : dataCache.getClass().getName(),
                dataQueue == null ? "NULL" : dataQueue.getClass().getName());
        configureRoutes(server);
        attachedServices.forEach( service -> {
            ((Service)service).configureRoutes(server);
        });
        server.start();
        return this;
    }

    public void stop() {
        if (server != null) {
            server.stop();
        }
    }

}

package com.hackorama.mcore.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.data.MemoryDataStore;
import com.hackorama.mcore.data.cache.DataCache;
import com.hackorama.mcore.data.queue.DataQueue;
import com.hackorama.mcore.server.Server;

public abstract class BaseService implements Service {

    private static Logger logger = LoggerFactory.getLogger(BaseService.class);

    static DataCache dataCache;
    static DataQueue dataQueue;
    static DataStore dataStore = new MemoryDataStore();

    private static void setCache(DataCache dataCache) {
        BaseService.dataCache = dataCache;
    }

    private static void setQueue(DataQueue dataQueue) {
        BaseService.dataQueue = dataQueue;
    }

    private static void setStore(DataStore dataStore) {
        BaseService.dataStore = dataStore;
    }

    private List<Service> attachedServices = new ArrayList<>();

    private Map<HttpMethod, Map<String, Function<Request, Response>>> routeHandlerMap = new HashMap<>();

    protected Server server;

    {
        routeHandlerMap.put(HttpMethod.GET, new HashMap<>());
        routeHandlerMap.put(HttpMethod.POST, new HashMap<>());
        routeHandlerMap.put(HttpMethod.PUT, new HashMap<>());
        routeHandlerMap.put(HttpMethod.DELETE, new HashMap<>());
    }

    @Override
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

    @Override
    public Service configureUsing(DataCache dataCache) {
        BaseService.setCache(dataCache);
        return this;
    }

    @Override
    public Service configureUsing(DataQueue dataQueue) {
        BaseService.setQueue(dataQueue);
        return this;
    }

    @Override
    public Service configureUsing(DataStore dataStore) {
        BaseService.setStore(dataStore);
        return this;
    }

    @Override
    public Service configureUsing(Server server) {
        this.server = server;
        return this;
    }

    public void DELETE(String path,
            Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response> handler) {
        ROUTE(HttpMethod.DELETE, path, handler);
    }

    public void GET(String path,
            Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response> handler) {
        ROUTE(HttpMethod.GET, path, handler);
    }
    public void POST(String path,
            Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response> handler) {
        ROUTE(HttpMethod.POST, path, handler);
    }

    public void PUT(String path,
            Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response> handler) {
        ROUTE(HttpMethod.PUT, path, handler);
    }

    public void ROUTE(HttpMethod method, String path,
            Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response> handler) {
        routeHandlerMap.get(method).put(path, handler);
    }

    @Override
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
            ((BaseService)service).configureRoutes(server);
        });
        server.start();
        return this;
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }

}

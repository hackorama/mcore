package com.hackorama.mcore.service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.uri.UriTemplate;
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

    private Map<HttpMethod, Map<String, Function<Request, Response>>> routeHandlerMap = new HashMap<>();
    {
        routeHandlerMap.put(HttpMethod.GET, new HashMap<>());
        routeHandlerMap.put(HttpMethod.POST, new HashMap<>());
        routeHandlerMap.put(HttpMethod.PUT, new HashMap<>());
        routeHandlerMap.put(HttpMethod.DELETE, new HashMap<>());
    }

    static DataCache dataCache;
    static DataQueue dataQueue;
    static DataStore dataStore = new MemoryDataStore();
    protected Server server;

    public abstract void configure();

    @Override
    public Service attach(Service service) {
        if (server == null) {
            throw new RuntimeException("Please configure a server before attaching a service");
        }
        service.configureUsing(server);
        return this;
    }

    @Override
    public Service configureUsing(Server server) {
        this.server = server;
        configure();
        server.setRoutes(routeHandlerMap);
        return this;
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
    public Service start() {
        if (server == null) {
            throw new RuntimeException("Please configure a server before starting the service");
        }
        logger.info("Starting service using server {}, data store {}, data cache {}, data queue {}",
                server.getClass().getName(), dataStore == null ? "NULL" : dataStore.getClass().getName(),
                dataCache == null ? "NULL" : dataCache.getClass().getName(),
                dataQueue == null ? "NULL" : dataQueue.getClass().getName());
        server.start();
        return this;
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }

    private static void setCache(DataCache dataCache) {
        BaseService.dataCache = dataCache;
    }

    private static void setQueue(DataQueue dataQueue) {
        BaseService.dataQueue = dataQueue;
    }

    private static void setStore(DataStore dataStore) {
        BaseService.dataStore = dataStore;
    }

    public void route(HttpMethod method, String path,
            Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response> handler) {
        routeHandlerMap.get(method).put(path, handler);
    }

    public static String formatPathVariable(String path) {
        UriTemplate uriTemplate = new UriTemplate(path);
        Map<String, String> parameters = new HashMap<>();
        uriTemplate.getTemplateVariables().forEach(e -> {
            parameters.put(e, ":" + e);
        });
        UriBuilder builder = UriBuilder.fromPath(path);
        URI output = builder.buildFromMap(parameters);
        return output.toString();
    }

}

package com.hackorama.mcore.service;

import java.util.function.Function;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.data.cache.DataCache;
import com.hackorama.mcore.data.queue.DataQueue;
import com.hackorama.mcore.server.Server;

public interface Service {
    public Service attach(Service service);

    public default Service configureUsing(DataCache dataCache) {
        return this;
    }

    public default Service configureUsing(DataQueue dataQueue) {
        return this;
    }

    public default Service configureUsing(DataStore dataStore) {
        return this;
    }

    public Service configureUsing(Server server);

    public void route(HttpMethod method, String path,
            Function<com.hackorama.mcore.common.Request, com.hackorama.mcore.common.Response> handler);

    public Service start();

    public void stop();
}

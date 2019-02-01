package com.hackorama.mcore.service;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import com.hackorama.mcore.common.TestUtil;
import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.data.mapdb.MapdbDataStore;
import com.hackorama.mcore.data.redis.RedisDataStoreCacheQueue;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.service.group.GroupService;

public class ServiceBuilderTest {

    @Test
    public void service_dynamicOrchestration_expectsNoErrors() throws FileNotFoundException, IOException {
        DataStore store = null;
        Service service = null;
        if (TestUtil.getEnv("REDIS_TEST")) {
            store = new RedisDataStoreCacheQueue();
            service = new GroupService().configureUsing(new SparkServer("test")).configureUsing(store)
                    .configureUsing(store.asQueue()).configureUsing(store.asCache()).start();
            assertNotNull(service);
        } else {
            store = new MapdbDataStore();
            service = new GroupService().configureUsing(new SparkServer("test")).configureUsing(store).start();
            assertNotNull(service);
        }
        service.stop();
    }

}

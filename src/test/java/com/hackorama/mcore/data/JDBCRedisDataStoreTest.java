package com.hackorama.mcore.data;

import com.hackorama.mcore.common.TestService;
import com.hackorama.mcore.data.redis.RedisDataStoreCacheQueue;

public class JDBCRedisDataStoreTest extends DataStoreTest {

    @Override
    protected void createDataStore() {
        isOptional = !TestService.getEnv("REDIS_TEST");
        try {
            dataStore = new RedisDataStoreCacheQueue();
        } catch (Exception e) {
            failedConnection = true;
            System.out.println("Redis data store connection failed");
            if (e.getMessage() == null) {
                e.printStackTrace();
            } else {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void datastore_usingSameTableNameForMultiKey_shouldNotBeAllowed() {
        throw new RuntimeException("Redis data store allows single and multi stores of same name");
    }

    @Override
    public void datastore_usingSameTableNameForSingleKey_shouldNotBeAllowed() {
        throw new RuntimeException("Redis data store allows single and multi stores of same name");
    }

}

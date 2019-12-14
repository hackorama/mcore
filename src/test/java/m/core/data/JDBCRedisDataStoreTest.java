package m.core.data;

import m.core.data.redis.RedisDataStoreCacheQueue;

public class JDBCRedisDataStoreTest extends DataStoreTest {

    @Override
    protected void createDataStore() {
        dataStore = new RedisDataStoreCacheQueue();
    }

    @Override
    protected String getType() {
        return "REDIS_TEST";
    }

    @Override
    public void datastore_usingSameTableNameForMultiKey_shouldNotBeAllowed() {
        super.datastore_usingSameTableNameForMultiKey_shouldNotBeAllowed();
        throw new RuntimeException("Redis data store allows single and multi stores of same name");
    }

    @Override
    public void datastore_usingSameTableNameForSingleKey_shouldNotBeAllowed() {
        super.datastore_usingSameTableNameForSingleKey_shouldNotBeAllowed();
        throw new RuntimeException("Redis data store allows single and multi stores of same name");
    }

}

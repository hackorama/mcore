package m.core.data.queue;

import m.core.data.redis.RedisDataStoreCacheQueue;

public class RedisQueueTest extends QueueTest  {

    @Override
    protected void createQueue() {
        queue = new RedisDataStoreCacheQueue();
    }

    @Override
    protected String getType() {
        return "REDIS_TEST";
    }

}

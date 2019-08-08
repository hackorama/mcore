package m.core.data.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RSetMultimap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import m.core.data.DataStore;
import m.core.data.cache.DataCache;
import m.core.data.queue.DataQueue;

public class RedisDataStoreCacheQueue implements DataStore, DataCache, DataQueue {

    private static final Logger logger = LoggerFactory.getLogger(RedisDataStoreCacheQueue.class);
    private static final int REDIS_DEFAULT_PORT = 6379;
    private Config config;
    private RedissonClient client;

    public RedisDataStoreCacheQueue() {
        client = Redisson.create();
    }

    public RedisDataStoreCacheQueue(String host) {
        this(host, REDIS_DEFAULT_PORT);
    }

    public RedisDataStoreCacheQueue(String host, int port) {
        config = new Config();
        config.useSingleServer().setAddress(host + ":" + port);
        client = Redisson.create(config);
    }

    @Override
    public DataCache asCache() {
        return this;
    }

    @Override
    public DataQueue asQueue() {
        return this;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }

    @Override
    public void close() {
        if (client != null) {
            client.shutdown();
        }
    }

    @Override
    public void consume(String channel, Function<String, Boolean> handler) {
        RTopic<String> topic = client.getTopic(channel);
        topic.addListener(new RoutingMessageListner(handler));
    }

    @Override
    public boolean contains(String store, String key) {
        RSetMultimap<String, String> multimap = client.getSetMultimap(store);
        if (multimap.isEmpty()) {
            return client.getKeys().isExists(formatKey(store, key)) > 0;
        } else {
            return multimap.containsKey(key);
        }
    }

    private String defomatKey(String storekey, String store) {
        return storekey.substring(store.length() + 1); // Get KEY from STORE:KEY
    }

    private String formatKey(String store) {
        return store + ":";
    }

    private String formatKey(String store, String key) {
        return formatKey(store) + key;
    }

    @Override
    public List<String> get(String store) {
        List<String> list = new ArrayList<>();
        RSetMultimap<String, String> multimap = client.getSetMultimap(store);
        if (multimap.isEmpty()) {
            client.getKeys().getKeysByPattern(formatKey(store) + "*").forEach(e -> list.add(getFromStore(e)));
        } else {
            list.addAll(multimap.values());
        }
        return list;
    }

    @Override
    public String get(String store, String key) {
        return getFromStore(formatKey(store, key));
    }

    @Override
    public List<String> getByValue(String store, String value) {
        List<String> list = new ArrayList<>();
        RSetMultimap<String, String> multimap = client.getSetMultimap(store);
        logger.debug("GETBYVALUE {} {}", store, value);
        if (multimap.isEmpty()) {
            client.getKeys().getKeysByPattern(formatKey(store) + "*").forEach(e -> {
                if (value.equals(getFromStore(e))) { // TODO Optimize
                    list.add(defomatKey(e, store));
                }
            });
        } else {
            multimap.entries().forEach(e -> {
                if (value.equals(e.getValue())) {
                    list.add(e.getKey());
                }
            });
        }
        return list;
    }

    private String getFromStore(String storeKey) {
        RBucket<String> bucket = client.getBucket(storeKey);
        logger.debug("GET {} : {}", storeKey, bucket.get());
        return bucket.get();
    }

    @Override
    public Set<String> getKeys(String store) {
        Set<String> keys = new HashSet<>();
        client.getKeys().getKeysByPattern(formatKey(store) + "*").forEach(e -> keys.add(e));
        return keys;
    }

    @Override
    public List<String> getMultiKey(String store, String key) {
        RSetMultimap<String, String> multimap = client.getSetMultimap(store);
        logger.debug("MULTI GET {} : {}", formatKey(store, key), multimap.get(key));
        return new ArrayList<String>(multimap.get(key));
    }

    @Override
    public void publish(String channel, String message) {
        RTopic<String> topic = client.getTopic(channel);
        topic.publish(message);
    }

    @Override
    public void put(String store, String key, String value) {
        logger.debug("PUT {} : {}", formatKey(store, key), value);
        RBucket<String> bucket = client.getBucket(formatKey(store, key));
        bucket.set(value);
    }

    @Override
    public void putMultiKey(String store, String key, String value) {
        logger.debug("MULTI PUT {} : {}", formatKey(store, key), value);
        RSetMultimap<String, String> multimap = client.getSetMultimap(store);
        multimap.put(key, value);
    }

    @Override
    public void remove(String store, String key) {
        RSetMultimap<String, String> multimap = client.getSetMultimap(store);
        if (multimap.isEmpty()) {
            RBucket<String> bucket = client.getBucket(formatKey(store, key));
            bucket.delete();
        } else {
            multimap.removeAll(key);
        }
    }

    @Override
    public void remove(String store, String key, String value) {
        RSetMultimap<String, String> multimap = client.getSetMultimap(store);
        if (multimap.isEmpty()) {
            RBucket<String> bucket = client.getBucket(formatKey(store, key));
            bucket.delete();
        } else {
            multimap.remove(key, value);
        }
    }

}

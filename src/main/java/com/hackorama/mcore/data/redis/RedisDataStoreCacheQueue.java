package com.hackorama.mcore.data.redis;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.data.cache.DataCache;
import com.hackorama.mcore.data.queue.DataQueue;

public class RedisDataStoreCacheQueue implements DataStore, DataCache, DataQueue {

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
    public boolean contains(String store, String key) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<String> get(String store) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String get(String store, String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getByValue(String store, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getMultiKey(String store, String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void put(String store, String key, String value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void putMultiKey(String store, String key, String value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void remove(String store, String key) {
        // TODO Auto-generated method stub

    }

    @Override
    public void remove(String store, String key, String value) {
        // TODO Auto-generated method stub

    }

    @Override
    public Set<String> getKeys(String store) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public void publish(String channel, String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void consume(String channel, Function<String, Boolean> handler) {
        // TODO Auto-generated method stub

    }

}

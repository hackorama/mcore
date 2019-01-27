package com.hackorama.mcore.data;


import java.util.List;
import java.util.Set;

public interface DataStore {
    public void clear();
    public boolean contains(String store, String key);
    public List<String> get(String store);
    public String get(String store, String key);
    public List<String> getByValue(String store, String value);
    public List<String> getMultiKey(String store, String key);
    public void put(String store, String key, String value);
    public void putMultiKey(String store, String key, String value);
    public void remove(String store, String key);
    public void remove(String store, String key, String value);
    public Set<String> getKeys(String store);
    public void close();
}

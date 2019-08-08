package m.core.data;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;

import m.core.data.cache.DataCache;
import m.core.data.queue.DataQueue;

public interface DataStore {

    public default DataCache asCache() {
        throw new NotImplementedException("This data store do not support data cache");
    }

    public default DataQueue asQueue() {
        throw new NotImplementedException("This data store do not support data queue");
    }

    public void clear();

    public void close();

    public boolean contains(String store, String key);

    public List<String> get(String store);

    public String get(String store, String key);

    public List<String> getByValue(String store, String value);

    public Set<String> getKeys(String store);

    public List<String> getMultiKey(String store, String key);

    public void put(String store, String key, String value);

    public void putMultiKey(String store, String key, String value);

    public void remove(String store, String key);

    public void remove(String store, String key, String value);

}

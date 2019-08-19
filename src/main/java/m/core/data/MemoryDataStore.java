package m.core.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * An in memory data-store.
 *
 * @see DataStore
 *
 * implNote Data is stored in memory and will not persist across application
 *           restarts, useful for testing.
 */
public class MemoryDataStore implements DataStore {

    private static Logger logger = LoggerFactory.getLogger(MemoryDataStore.class);

    Map<String, Map<String, String>> singleKeyStores = new HashMap<>();
    Map<String, Multimap<String, String>> multiKeyStores = new HashMap<>();
    Set<String> stores = new HashSet<>();

    /**
     * Constructs an in memory data-store.
     */
    public MemoryDataStore() {
    }

    @Override
    public void clear() {
        singleKeyStores = new HashMap<>();
        multiKeyStores = new HashMap<>();
        stores = new HashSet<>();
        logger.info("The data store is reset to empty");
    }

    @Override
    public boolean contains(String store, String key) {
        if (singleKeyStores.containsKey(store)) {
            return singleKeyStores.get(store).containsKey(key);
        } else if (multiKeyStores.containsKey(store)) {
            return multiKeyStores.get(store).containsKey(key);
        }
        logger.warn("Not a valid data store {}", store);
        return false;
    }

    @Override
    public List<String> get(String store) {
        if (singleKeyStores.containsKey(store)) {
            return new ArrayList<String>(singleKeyStores.get(store).values());
        } else if (multiKeyStores.containsKey(store)) {
            return new ArrayList<String>(multiKeyStores.get(store).values());
        }
        return new ArrayList<>();
    }

    @Override
    public String get(String store, String key) {
        if (singleKeyStores.containsKey(store)) {
            return singleKeyStores.get(store).get(key);
        }
        logger.warn("Not a valid data store {}", store);
        return null;
    }

    @Override
    public List<String> getByValue(String store, String value) {
        if (singleKeyStores.containsKey(store)) {
            return singleKeyStores.get(store).entrySet().stream().filter(e -> e.getValue().equals(value))
                    .map(Entry::getKey).collect(Collectors.toList());
        } else if (multiKeyStores.containsKey(store)) {
            return multiKeyStores.get(store).entries().stream().filter(e -> e.getValue().equals(value))
                    .map(Entry::getKey).collect(Collectors.toList());
        }
        logger.warn("Not a valid data store {}", store);
        return new ArrayList<>();
    }

    @Override
    public List<String> getMulti(String store, String key) {
        if (multiKeyStores.containsKey(store)) {
            return (List<String>) multiKeyStores.get(store).get(key);
        }
        logger.warn("Not a valid data store {}", store);
        return new ArrayList<>();
    }

    @Override
    public void put(String store, String key, String value) {
        if (!singleKeyStores.containsKey(store)) {
            if (multiKeyStores.containsKey(store)) {
                throw new RuntimeException("Another multi key store already exists with the same name " + store);
            }
            logger.info("Created data store {}", store);
            singleKeyStores.put(store, new HashMap<String, String>());
        }
        singleKeyStores.get(store).put(key, value);
    }

    @Override
    public void putMulti(String store, String key, String value) {
        if (!multiKeyStores.containsKey(store)) {
            if (singleKeyStores.containsKey(store)) {
                throw new RuntimeException("Another store already exists with the same name " + store);
            }
            logger.info("Created multi key data store {}", store);
            multiKeyStores.put(store, ArrayListMultimap.create());
        }
        multiKeyStores.get(store).put(key, value);
    }

    @Override
    public void remove(String store, String key) {
        if (singleKeyStores.containsKey(store)) {
            singleKeyStores.get(store).remove(key);
        } else if (multiKeyStores.containsKey(store)) {
            multiKeyStores.get(store).removeAll(key);
        } else {
            logger.warn("Not a valid data store {}", store);
        }
    }

    @Override
    public void remove(String store, String key, String value) {
        if (singleKeyStores.containsKey(store)) {
            singleKeyStores.get(store).remove(key);
        } else if (multiKeyStores.containsKey(store)) {
            multiKeyStores.get(store).remove(key, value);
        } else {
            logger.warn("Not a valid data store {}", store);
        }
    }

    @Override
    public Set<String> getKeys(String store) {
        if (singleKeyStores.containsKey(store)) {
            return singleKeyStores.get(store).keySet();
        } else if (multiKeyStores.containsKey(store)) {
            return multiKeyStores.get(store).keySet();
        }
        logger.warn("Not a valid data store {}", store);
        return new HashSet<>();
    }

    @Override
    public void close() {
        logger.info("Data store closed");
    }

}

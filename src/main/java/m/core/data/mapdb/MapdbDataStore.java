package m.core.data.mapdb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.mapdb.DB;
import org.mapdb.DBException;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.mapdb.serializer.SerializerArrayTuple;

import m.core.data.DataStore;

/**
 * A MapDB based data-store.
 *
 * @see DataStore
 */
public class MapdbDataStore implements DataStore {

    private DB db;
    private Set<String> multiKeyStoreNames = new HashSet<>();

    /**
     * Constructs a MapDB data-store with default properties.
     */
    public MapdbDataStore() {
        db = DBMaker.memoryDB().make();
    }

    /**
     * Constructs a MapDB data-store with specified db file.
     *
     * @param dbFile the db file
     */
    public MapdbDataStore(String dbFile) {
        db = DBMaker.fileDB(dbFile).make();
    }

    @Override
    public void clear() {
        db.getStore().getAllFiles().forEach(System.out::println);
        db = DBMaker.memoryDB().make();
        db.getStore().getAllFiles().forEach(System.out::println);
    }

    @Override
    public boolean contains(String store, String key) {
        if (multiKeyStoreNames.contains(store)) {
            SortedSet<Object[]> set = ((NavigableSet<Object[]>) db.treeSet(store)
                    .serializer(new SerializerArrayTuple(Serializer.STRING, Serializer.STRING)).open())
                            .subSet(new Object[] { key }, new Object[] { key, null });
            return !set.isEmpty();
        }
        return db.hashMap(store, Serializer.STRING, Serializer.STRING).open().containsKey(key);
    }

    @Override
    public List<String> get(String store) {
        try {
            return new ArrayList<String>(db.hashMap(store, Serializer.STRING, Serializer.STRING).open().values());
        } catch (DBException.WrongConfiguration e) {
            return new ArrayList<>();
        }
    }

    @Override
    public String get(String store, String key) {
        try {
            return db.hashMap(store, Serializer.STRING, Serializer.STRING).open().get(key);
        } catch (DBException.WrongConfiguration e) {
            return null;
        }
    }

    @Override
    public List<String> getByValue(String store, String value) {
        try {
            if (multiKeyStoreNames.contains(store)) {
                // TODO Check MapDB code/doc for more efficient method
                SortedSet<Object[]> keyValueSet = ((NavigableSet<Object[]>) db.treeSet(store)
                        .serializer(new SerializerArrayTuple(Serializer.STRING, Serializer.STRING)).open());
                List<String> result = new ArrayList<>();
                for (Object[] keyValue : keyValueSet) {
                    if (value.equals(keyValue[1].toString())) {
                        result.add(keyValue[0].toString());
                    }
                }
                return result;
            }
            return ((Set<Entry<String, String>>) db.hashMap(store, Serializer.STRING, Serializer.STRING).open()
                    .entrySet()).stream().filter(e -> e.getValue().equals(value)).map(Entry::getKey)
                            .collect(Collectors.toList());
        } catch (DBException.WrongConfiguration e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Set<String> getKeys(String store) {
        if (multiKeyStoreNames.contains(store)) {
            Set<String> result = new HashSet<>();
            SortedSet<Object[]> keyValueSet = ((NavigableSet<Object[]>) db.treeSet(store)
                    .serializer(new SerializerArrayTuple(Serializer.STRING, Serializer.STRING)).open());
            for (Object[] keyValue : keyValueSet) {
                result.add(keyValue[0].toString());
            }
            return result;
        }
        try {
            return db.hashMap(store, Serializer.STRING, Serializer.STRING).open().keySet();
        } catch (DBException.WrongConfiguration e) {
            return new HashSet<>();
        }
    }

    @Override
    public List<String> getMulti(String store, String key) {
        try {
            SortedSet<Object[]> keyValueSet = ((NavigableSet<Object[]>) db.treeSet(store)
                    .serializer(new SerializerArrayTuple(Serializer.STRING, Serializer.STRING)).open())
                            .subSet(new Object[] { key }, new Object[] { key, null });
            List<String> result = new ArrayList<>();
            for (Object[] keyValue : keyValueSet) {
                result.add(keyValue[1].toString());
            }
            return result;
        } catch (DBException.WrongConfiguration e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void put(String store, String key, String value) {
        db.hashMap(store, Serializer.STRING, Serializer.STRING).createOrOpen().put(key, value);
    }

    @Override
    public void putMulti(String store, String key, String value) {
        ((NavigableSet<Object[]>) db.treeSet(store)
                .serializer(new SerializerArrayTuple(Serializer.STRING, Serializer.STRING)).createOrOpen())
                        .add(new Object[] { key, value });
        multiKeyStoreNames.add(store);
    }

    @Override
    public void remove(String store, String key) {
        try {
            if (multiKeyStoreNames.contains(store)) {
                // TODO Check MapDB code/doc for more efficient method
                getMulti(store, key).forEach(value -> {
                    remove(store, key, value);
                });
            } else {
                db.hashMap(store, Serializer.STRING, Serializer.STRING).open().remove(key);
            }
        } catch (DBException.WrongConfiguration e) {
            // TOD log warning
        }
    }

    @Override
    public void remove(String store, String key, String value) {
        try {
            ((NavigableSet<Object[]>) db.treeSet(store)
                    .serializer(new SerializerArrayTuple(Serializer.STRING, Serializer.STRING)).open())
                            .remove(new Object[] { key, value });
        } catch (DBException.WrongConfiguration e) {
            // TOD log warning
        }
    }

    @Override
    public void close() {
        if (db != null) {
            db.close();
        }
    }

}

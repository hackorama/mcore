package com.hackorama.mcore.data.mapdb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.mapdb.serializer.SerializerArrayTuple;

import com.hackorama.mcore.data.DataStore;

/**
 *
 * TODO: Implement a data store using MapDB http://www.mapdb.org/
 *
 * @author Kishan Thomas <kishan.thomas@gmail.com>
 *
 */
public class MapdbDataStore implements DataStore {

    private DB db;
    private Set<String> multiKeyStoreNames = new HashSet<>();

    public MapdbDataStore() {
        db = DBMaker.memoryDB().make();
    }

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
        return new ArrayList<String>(db.hashMap(store, Serializer.STRING, Serializer.STRING).open().values());
    }

    @Override
    public String get(String store, String key) {
        return db.hashMap(store, Serializer.STRING, Serializer.STRING).open().get(key);
    }

    @Override
    public List<String> getByValue(String store, String value) {
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
        return ((Set<Entry<String, String>>) db.hashMap(store, Serializer.STRING, Serializer.STRING).open().entrySet())
                .stream().filter(e -> e.getValue().equals(value)).map(Entry::getKey).collect(Collectors.toList());
    }

    @Override
    public Set<String> getKeys(String store) {
        return db.hashMap(store, Serializer.STRING, Serializer.STRING).open().keySet();
    }

    @Override
    public List<String> getMultiKey(String store, String key) {
        SortedSet<Object[]> keyValueSet = ((NavigableSet<Object[]>) db.treeSet(store)
                .serializer(new SerializerArrayTuple(Serializer.STRING, Serializer.STRING)).open())
                        .subSet(new Object[] { key }, new Object[] { key, null });
        List<String> result = new ArrayList<>();
        for (Object[] keyValue : keyValueSet) {
            result.add(keyValue[1].toString());
        }
        return result;
    }

    @Override
    public void put(String store, String key, String value) {
        db.hashMap(store, Serializer.STRING, Serializer.STRING).createOrOpen().put(key, value);
    }

    @Override
    public void putMultiKey(String store, String key, String value) {
        multiKeyStoreNames.add(store);
        ((NavigableSet<Object[]>) db.treeSet(store)
                .serializer(new SerializerArrayTuple(Serializer.STRING, Serializer.STRING)).createOrOpen())
                        .add(new Object[] { key, value });
    }

    @Override
    public void remove(String store, String key) {
        if (multiKeyStoreNames.contains(store)) {
            // TODO Check MapDB code/doc for more efficient method
            getMultiKey(store, key).forEach(value -> {
                remove(store, key, value);
            });
        } else {
            db.hashMap(store, Serializer.STRING, Serializer.STRING).open().remove(key);
        }
    }

    @Override
    public void remove(String store, String key, String value) {
        ((NavigableSet<Object[]>) db.treeSet(store)
                .serializer(new SerializerArrayTuple(Serializer.STRING, Serializer.STRING)).open())
                        .remove(new Object[] { key, value });
    }

}

package com.hackorama.mcore.data.mapdb;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;

import com.hackorama.mcore.data.DataStore;

/**
 *
 * TODO: Implement a data store using MapDB http://www.mapdb.org/
 *
 * @author Kishan Thomas <kishan.thomas@gmail.com>
 *
 */
public class MapdbDataStore implements DataStore {

    public MapdbDataStore() {
        throw new NotImplementedException("MapDB data store is not yet implemented, please use the in memory data store option");
    }

    public MapdbDataStore(String dbFile) {
        this();
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

}

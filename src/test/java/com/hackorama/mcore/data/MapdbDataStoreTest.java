package com.hackorama.mcore.data;

import java.sql.SQLException;

import com.hackorama.mcore.data.mapdb.MapdbDataStore;

public class MapdbDataStoreTest extends DataStoreTest {

    @Override
    protected void createDataStore() throws SQLException {
        dataStore = new MapdbDataStore();
    }

}

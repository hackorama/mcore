package com.hackorama.mcore.data;

import java.sql.SQLException;

import com.hackorama.mcore.data.jdbc.JDBCDataStore;

public class JDBCMemoryH2DataStoreTest extends DataStoreTest {

    @Override
    protected void createDataStore() throws SQLException {
        dataStore = new JDBCDataStore();
    }

}

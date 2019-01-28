package com.hackorama.mcore.data;

import java.sql.SQLException;

import com.hackorama.mcore.data.jdbc.JDBCDataStore;

public class JDBCDataStoreTest extends DataStoreTest {

    @Override
    protected void createDataStore() throws SQLException {
        dataStore = new JDBCDataStore("jdbc:h2:./data.test.h2");
    }

}

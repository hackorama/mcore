package com.hackorama.mcore.data;

import java.sql.SQLException;

import com.hackorama.mcore.data.jdbc.JDBCDataStore;

public class JDBCDataStoreTest extends DataStoreTest {

    @Override
    protected void createDataStore() throws SQLException {
        dataStore = new JDBCDataStore("jdbc:h2:./data.test.h2");
        // Test using MySQL
        // dataStore = new JDBCDataStore("jdbc:mysql://localhost/test",
        // "com.mysql.cj.jdbc.Driver", "test", "test");
        // Test using Postgesql
        // dataStore = new JDBCDataStore("jdbc:postgresql://localhost:5432/test",
        // "org.postgresql.Driver", "test", "test");
    }

}

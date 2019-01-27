package com.hackorama.mcore.data;

import java.sql.SQLException;

import com.hackorama.mcore.data.jdbc.JDBCDataStore;

public class JDBCDataStoreTest extends DataStoreTest {

    @Override
    protected void createDataStore() throws SQLException {
        dataStore = new JDBCDataStore();
        // Test using MySQL
        // dataStore = new JDBCDataStore("jdbc:mysql://localhost/test",
        // "com.mysql.cj.jdbc.Driver", "test", "test");
        // dataStore = new
        // JDBCDataStore("jdbc:mysql://sql3.freesqldatabase.com/sql3275761",
        // "com.mysql.cj.jdbc.Driver", "sql3275761", "hIvqjGH5wx");
        // dataStore = new
        // JDBCDataStore("jdbc:postgresql://baasu.db.elephantsql.com:5432/godwbhlk",
        // "org.postgresql.Driver", "godwbhlk", "syVFX7pI-cnqIfq_8ZYK3dCVTIEbN6e8");
    }

}

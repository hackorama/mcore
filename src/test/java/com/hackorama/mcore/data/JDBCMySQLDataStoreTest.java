package com.hackorama.mcore.data;

import java.sql.SQLException;

import com.hackorama.mcore.common.TestUtil;
import com.hackorama.mcore.data.jdbc.JDBCDataStore;

public class JDBCMySQLDataStoreTest extends DataStoreTest {


    @Override
    protected void createDataStore() {
        isOptional = true;
        try {
            dataStore = new JDBCDataStore(TestUtil.getEnv("MYSQL_URL", "jdbc:mysql://localhost/test"),
                    "com.mysql.cj.jdbc.Driver", TestUtil.getEnv("MYSQL_USER", "travis"), TestUtil.getEnv("MYSQL_PASS", ""));
        } catch (SQLException e) {
            failedConnection = true;
            System.out.println(e.getMessage());
        }
    }

}

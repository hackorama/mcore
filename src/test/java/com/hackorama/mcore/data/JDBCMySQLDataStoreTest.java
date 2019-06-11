package com.hackorama.mcore.data;

import java.sql.SQLException;

import com.hackorama.mcore.common.TestService;
import com.hackorama.mcore.data.jdbc.JDBCDataStore;

public class JDBCMySQLDataStoreTest extends DataStoreTest {


    @Override
    protected void createDataStore() {
        isOptional = ! TestService.getEnv("MYSQL_TEST");
        try {
            dataStore = new JDBCDataStore(TestService.getEnv("MYSQL_URL", "jdbc:mysql://localhost/test"),
                    "com.mysql.cj.jdbc.Driver", TestService.getEnv("MYSQL_USER", "travis"), TestService.getEnv("MYSQL_PASS", ""));
        } catch (SQLException e) {
            failedConnection = true;
            System.out.println(e.getMessage());
        }
    }

}

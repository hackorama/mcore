package com.hackorama.mcore.data;

import java.sql.SQLException;

import com.hackorama.mcore.common.TestUtil;
import com.hackorama.mcore.data.jdbc.JDBCDataStore;

public class JDBCPostgresqlDataStoreTest extends DataStoreTest {


    @Override
    protected void createDataStore() {
        isOptional = ! TestUtil.getEnv("PG_TEST");
        try {
            dataStore = new JDBCDataStore(TestUtil.getEnv("PG_URL", "jdbc:postgresql://localhost:5432/test"),
                    "com.postgresql.Driver", TestUtil.getEnv("PG_USER", "postgres"), TestUtil.getEnv("PG_PASS", ""));
        } catch (SQLException e) {
            failedConnection = true;
            System.out.println(e.getMessage());
        }
    }

}

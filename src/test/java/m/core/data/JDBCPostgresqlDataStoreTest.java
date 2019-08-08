package m.core.data;

import java.sql.SQLException;

import m.core.common.TestService;
import m.core.data.jdbc.JDBCDataStore;

public class JDBCPostgresqlDataStoreTest extends DataStoreTest {


    @Override
    protected void createDataStore() {
        isOptional = ! TestService.getEnv("PG_TEST");
        try {
            dataStore = new JDBCDataStore(TestService.getEnv("PG_URL", "jdbc:postgresql://localhost:5432/test"),
                    "com.postgresql.Driver", TestService.getEnv("PG_USER", "postgres"), TestService.getEnv("PG_PASS", ""));
        } catch (SQLException e) {
            failedConnection = true;
            System.out.println(e.getMessage());
        }
    }

}

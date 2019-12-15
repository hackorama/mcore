package m.core.data;

import java.sql.SQLException;

import m.core.common.TestService;
import m.core.data.jdbc.JDBCDataStore;

public class JDBCMySQLDataStoreTest extends DataStoreTest {

    @Override
    protected void createDataStore() throws SQLException {
        dataStore = new JDBCDataStore(TestService.getEnv("MYSQL_URL", "jdbc:mysql://localhost/test"),
                "com.mysql.cj.jdbc.Driver", TestService.getEnv("MYSQL_USER", "travis"),
                TestService.getEnv("MYSQL_PASS", ""));
    }

    @Override
    protected String getType() {
        return "MYSQL_TEST";
    }

}

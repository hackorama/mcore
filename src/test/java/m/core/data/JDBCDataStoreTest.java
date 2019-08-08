package m.core.data;

import java.sql.SQLException;

import m.core.data.jdbc.JDBCDataStore;

public class JDBCDataStoreTest extends DataStoreTest {

    @Override
    protected void createDataStore() throws SQLException {
        dataStore = new JDBCDataStore("jdbc:h2:./data.test.h2");
    }

}

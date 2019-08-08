package m.core.data;

import java.sql.SQLException;

import m.core.data.jdbc.JDBCDataStore;

public class JDBCMemoryH2DataStoreTest extends DataStoreTest {

    @Override
    protected void createDataStore() throws SQLException {
        dataStore = new JDBCDataStore();
    }

}

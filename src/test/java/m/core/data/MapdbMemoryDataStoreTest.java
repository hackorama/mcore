package m.core.data;

import java.sql.SQLException;

import m.core.data.mapdb.MapdbDataStore;

public class MapdbMemoryDataStoreTest extends DataStoreTest {

    @Override
    protected void createDataStore() throws SQLException {
        dataStore = new MapdbDataStore();
    }

}

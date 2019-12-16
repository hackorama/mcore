package m.core.data;

import java.sql.SQLException;

import m.core.data.mongodb.MongoDataStore;

public class MongoDataStoreTest extends DataStoreTest {

    @Override
    protected void createDataStore() throws SQLException {
        dataStore = new MongoDataStore("mcore");
    }

    @Override
    protected String getType() {
        return "MONGO_TEST";
    }

    @Override
    public void datastore_usingSameTableNameForMultiKey_shouldNotBeAllowed() {
        super.datastore_usingSameTableNameForMultiKey_shouldNotBeAllowed();
        throw new RuntimeException("Mongo data store allows single and multi stores of same name");
    }

    @Override
    public void datastore_usingSameTableNameForSingleKey_shouldNotBeAllowed() {
        super.datastore_usingSameTableNameForSingleKey_shouldNotBeAllowed();
        throw new RuntimeException("Mongo data store allows single and multi stores of same name");
    }

}

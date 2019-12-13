package m.core.data;

import java.sql.SQLException;

import org.junit.BeforeClass;

import m.core.common.TestService;
import m.core.data.mongodb.MongoDataStore;

public class MongoDataStoreTest extends DataStoreTest {

    @BeforeClass
    public static void setup() throws Exception {
        if (!TestService.getEnv("MONGO_TEST")) {
            System.out.println("Skipping data tests since MONGO_TEST server is not available");
            org.junit.Assume.assumeTrue(false);
        }
    }

    @Override
    protected void createDataStore() throws SQLException {
        dataStore = new MongoDataStore("mcore");
    }

}

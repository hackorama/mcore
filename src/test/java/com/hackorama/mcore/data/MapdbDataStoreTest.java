package com.hackorama.mcore.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hackorama.mcore.data.mapdb.MapdbDataStore;

public class MapdbDataStoreTest extends DataStoreTest {

    @Before
    public void setUp() throws Exception {
        if (dataStore == null) {
            dataStore = new MapdbDataStore();
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void datastore_insertedValues_matchesOnGettingByKey() {
        super.datastore_insertedValues_matchesOnGettingByKey();
    }

    @Test
    public void datastore_insertedMultiKeyValues_matchesOnGettingByKey() {
        super.datastore_insertedMultiKeyValues_matchesOnGettingByKey();
    }

    @Test (expected = RuntimeException.class)
    public void datastore_usingSameTableNameForMultiKey_shouldNotBeAllowed() {
        super.datastore_usingSameTableNameForMultiKey_shouldNotBeAllowed();
    }

    @Test (expected = RuntimeException.class)
    public void datastore_usingSameTableNameForSingleKey_shouldNotBeAllowed() {
        super.datastore_usingSameTableNameForSingleKey_shouldNotBeAllowed();
    }

    @Test
    public void datastore_usingUnknownTable_shouldBeHandled() {
        super.datastore_usingUnknownTable_shouldBeHandled();
    }

}

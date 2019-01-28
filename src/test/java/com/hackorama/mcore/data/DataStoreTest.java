package com.hackorama.mcore.data;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataStoreTest {

    protected boolean isOptional;
    protected boolean failedConnection;
    protected DataStore dataStore;

    protected void createDataStore() throws SQLException {
        dataStore = new MemoryDataStore();
    }

    @Before
    public void setUp() throws Exception {
        createDataStore();
    }

    @After
    public void tearDown() throws Exception {
        clearTestData();
    }


    public void clearTestData() {
        if(isOptional && failedConnection) {
            return;
        }
        clearTestData("TEST");
        clearTestData("MULTI_TEST");
        clearTestData("MULTI_TABLE_ONE");
        clearTestData("MULTI_TABLE_TWO");
        clearTestData("TABLE_TWO");
        clearTestData("TABLE_MULTI");
        clearTestData("TABLE_SINGLE");
        dataStore.close();
    }

    private void clearTestData(String store) {
       Set<String> keys =  new HashSet<>();
       // make a copy of keys for concurrent modification
       dataStore.getKeys(store).forEach(e -> keys.add(e));
       keys.forEach(e -> dataStore.remove(store, e));
    }

    @Test
    public void datastore_insertedMultiKeyValues_matchesOnGettingByKey() {

        if(isOptional && failedConnection) {
            return;
        }

        dataStore.putMultiKey("MULTI_TEST", "ONE", "1_1");
        dataStore.putMultiKey("MULTI_TEST", "TWO", "2_1");
        dataStore.putMultiKey("MULTI_TEST", "TWO", "2_2");
        dataStore.putMultiKey("MULTI_TEST", "THREE", "3_1");
        dataStore.putMultiKey("MULTI_TEST", "THREE", "3_2");
        dataStore.putMultiKey("MULTI_TEST", "THREE", "3_3");
        assertEquals(1, dataStore.getMultiKey("MULTI_TEST", "ONE").size());
        assertEquals(2, dataStore.getMultiKey("MULTI_TEST", "TWO").size());
        assertEquals(3, dataStore.getMultiKey("MULTI_TEST", "THREE").size());
        assertTrue(dataStore.getMultiKey("MULTI_TEST", "THREE").contains("3_1"));
        assertTrue(dataStore.getMultiKey("MULTI_TEST", "THREE").contains("3_2"));
        assertTrue(dataStore.getMultiKey("MULTI_TEST", "THREE").contains("3_3"));
        dataStore.remove("MULTI_TEST", "TWO");
        assertEquals(0, dataStore.getMultiKey("MULTI_TEST", "TWO").size());
        dataStore.remove("MULTI_TEST", "THREE", "3_2");
        assertEquals(2, dataStore.getMultiKey("MULTI_TEST", "THREE").size());
        assertTrue(dataStore.getMultiKey("MULTI_TEST", "THREE").contains("3_1"));
        assertFalse(dataStore.getMultiKey("MULTI_TEST", "THREE").contains("3_2"));
        assertTrue(dataStore.getMultiKey("MULTI_TEST", "THREE").contains("3_3"));

        dataStore.putMultiKey("MULTI_TABLE_ONE", "1_1", "one_one");
        dataStore.putMultiKey("MULTI_TABLE_ONE", "1_2", "one_two");
        dataStore.putMultiKey("MULTI_TABLE_ONE", "1_3", "one_three");
        dataStore.putMultiKey("MULTI_TABLE_ONE", "1_4", "one_same");
        dataStore.putMultiKey("MULTI_TABLE_ONE", "1_5", "one_same");
        dataStore.putMultiKey("MULTI_TABLE_ONE", "1_6", "one_same");
        dataStore.putMultiKey("MULTI_TABLE_ONE", "1_7", "one_seven");

        dataStore.putMultiKey("MULTI_TABLE_TWO", "1_1", "two_one");
        dataStore.putMultiKey("MULTI_TABLE_TWO", "1_2", "two_two");
        dataStore.putMultiKey("MULTI_TABLE_TWO", "1_3", "two_three");
        dataStore.putMultiKey("MULTI_TABLE_TWO", "1_0", "two_four");
        dataStore.putMultiKey("MULTI_TABLE_TWO", "1_0", "two_five");
        dataStore.putMultiKey("MULTI_TABLE_TWO", "1_0", "two_six");
        dataStore.putMultiKey("MULTI_TABLE_TWO", "1_7", "two_seven");

        assertTrue(dataStore.contains("MULTI_TABLE_ONE", "1_1"));
        assertFalse(dataStore.contains("MULTI_TABLE_ONE", "false"));

        assertEquals("one_one", dataStore.getMultiKey("MULTI_TABLE_ONE", "1_1").get(0));
        assertEquals("one_two", dataStore.getMultiKey("MULTI_TABLE_ONE", "1_2").get(0));
        assertEquals("one_three", dataStore.getMultiKey("MULTI_TABLE_ONE", "1_3").get(0));

        assertEquals("two_one", dataStore.getMultiKey("MULTI_TABLE_TWO", "1_1").get(0));
        assertEquals("two_two", dataStore.getMultiKey("MULTI_TABLE_TWO", "1_2").get(0));
        assertEquals("two_three", dataStore.getMultiKey("MULTI_TABLE_TWO", "1_3").get(0));

        dataStore.remove("MULTI_TABLE_ONE", "1_2");
        assertEquals("one_one", dataStore.getMultiKey("MULTI_TABLE_ONE", "1_1").get(0));
        assertEquals(0, dataStore.getMultiKey("MULTI_TABLE_ONE", "1_2").size());
        assertEquals("one_three", dataStore.getMultiKey("MULTI_TABLE_ONE", "1_3").get(0));
        assertFalse(dataStore.contains("MULTI_TABLE_ONE", "1_2"));

        assertEquals("two_one", dataStore.getMultiKey("MULTI_TABLE_TWO", "1_1").get(0));
        assertEquals("two_two", dataStore.getMultiKey("MULTI_TABLE_TWO", "1_2").get(0));
        assertEquals("two_three", dataStore.getMultiKey("MULTI_TABLE_TWO", "1_3").get(0));

        assertEquals(3, dataStore.getByValue("MULTI_TABLE_ONE", "one_same").size());
        assertTrue(dataStore.getByValue("MULTI_TABLE_ONE", "one_same").contains("1_4"));
        assertTrue(dataStore.getByValue("MULTI_TABLE_ONE", "one_same").contains("1_5"));
        assertTrue(dataStore.getByValue("MULTI_TABLE_ONE", "one_same").contains("1_6"));

        assertEquals(0, dataStore.getByValue("MULTI_TABLE_TWO", "two_same").size());
        assertEquals(3, dataStore.getMultiKey("MULTI_TABLE_TWO", "1_0").size());
        assertTrue(dataStore.getMultiKey("MULTI_TABLE_TWO", "1_0").contains("two_four"));
        assertTrue(dataStore.getMultiKey("MULTI_TABLE_TWO", "1_0").contains("two_five"));
        assertTrue(dataStore.getMultiKey("MULTI_TABLE_TWO", "1_0").contains("two_six"));

    }

    @Test
    public void datastore_insertedValues_matchesOnGettingByKey() {

        if(isOptional && failedConnection) {
            return;
        }

        dataStore.put("TEST", "ONE", "UNO");
        dataStore.put("TEST", "TWO", "DOS");
        dataStore.put("TEST", "THREE", "TRES");
        assertEquals("UNO", dataStore.get("TEST", "ONE"));
        assertEquals("DOS", dataStore.get("TEST", "TWO"));
        assertEquals("TRES", dataStore.get("TEST", "THREE"));
        assertEquals(3, dataStore.get("TEST").size());
        assertTrue(dataStore.get("TEST").contains("UNO"));
        assertTrue(dataStore.get("TEST").contains("DOS"));
        assertTrue(dataStore.get("TEST").contains("TRES"));
        dataStore.put("TEST", "FOUR", "QUATTRO_1");
        dataStore.put("TEST", "FOUR", "QUATTRO_2");
        assertEquals("QUATTRO_2", dataStore.get("TEST", "FOUR"));
        dataStore.remove("TEST", "THREE");
        assertEquals(null, dataStore.get("TEST", "THREE"));

        dataStore.put("TABLE_ONE", "1_1", "one_one");
        dataStore.put("TABLE_ONE", "1_2", "one_two");
        dataStore.put("TABLE_ONE", "1_3", "one_three");
        dataStore.put("TABLE_ONE", "1_4", "one_same");
        dataStore.put("TABLE_ONE", "1_5", "one_same");
        dataStore.put("TABLE_ONE", "1_6", "one_same");
        dataStore.put("TABLE_ONE", "1_7", "one_seven");

        dataStore.put("TABLE_TWO", "1_1", "two_one");
        dataStore.put("TABLE_TWO", "1_2", "two_two");
        dataStore.put("TABLE_TWO", "1_3", "two_three");
        dataStore.put("TABLE_TWO", "1_4", "two_same");
        dataStore.put("TABLE_TWO", "1_5", "two_same");
        dataStore.put("TABLE_TWO", "1_6", "two_same");
        dataStore.put("TABLE_TWO", "1_7", "two_seven");

        assertTrue(dataStore.contains("TABLE_ONE", "1_1"));
        assertFalse(dataStore.contains("TABLE_ONE", "false"));

        assertEquals("one_one", dataStore.get("TABLE_ONE", "1_1"));
        assertEquals("one_two", dataStore.get("TABLE_ONE", "1_2"));
        assertEquals("one_three", dataStore.get("TABLE_ONE", "1_3"));

        assertEquals("two_one", dataStore.get("TABLE_TWO", "1_1"));
        assertEquals("two_two", dataStore.get("TABLE_TWO", "1_2"));
        assertEquals("two_three", dataStore.get("TABLE_TWO", "1_3"));

        dataStore.remove("TABLE_ONE", "1_2");
        assertEquals("one_one", dataStore.get("TABLE_ONE", "1_1"));
        assertEquals(null, dataStore.get("TABLE_ONE", "1_2"));
        assertEquals("one_three", dataStore.get("TABLE_ONE", "1_3"));
        assertFalse(dataStore.contains("TABLE_ONE", "1_2"));

        assertEquals("two_one", dataStore.get("TABLE_TWO", "1_1"));
        assertEquals("two_two", dataStore.get("TABLE_TWO", "1_2"));
        assertEquals("two_three", dataStore.get("TABLE_TWO", "1_3"));

        assertEquals(3, dataStore.getByValue("TABLE_ONE", "one_same").size());
        assertTrue(dataStore.getByValue("TABLE_ONE", "one_same").contains("1_4"));
        assertTrue(dataStore.getByValue("TABLE_ONE", "one_same").contains("1_5"));
        assertTrue(dataStore.getByValue("TABLE_ONE", "one_same").contains("1_6"));

        assertEquals(3, dataStore.getByValue("TABLE_TWO", "two_same").size());
        assertTrue(dataStore.getByValue("TABLE_TWO", "two_same").contains("1_4"));
        assertTrue(dataStore.getByValue("TABLE_TWO", "two_same").contains("1_5"));
        assertTrue(dataStore.getByValue("TABLE_ONE", "one_same").contains("1_6"));

    }

    @Test (expected = RuntimeException.class)
    public void datastore_usingSameTableNameForMultiKey_shouldNotBeAllowed() {

        if(isOptional && failedConnection) {
            throw new RuntimeException("Expected");
        }

        dataStore.put("TABLE_SINGLE", "1_1", "one_one");
        dataStore.putMultiKey("TABLE_SINGLE", "1_1", "one_one");
    }

    @Test (expected = RuntimeException.class)
    public void datastore_usingSameTableNameForSingleKey_shouldNotBeAllowed() {

        if(isOptional && failedConnection) {
            throw new RuntimeException("Expected");
        }

        dataStore.putMultiKey("TABLE_MULTI", "1_1", "one_one");
        dataStore.put("TABLE_MULTI", "1_1", "one_one");
    }

    @Test
    public void datastore_usingUnknownTable_shouldBeHandled() {

        if(isOptional && failedConnection) {
            return;
        }

        dataStore.get("NON_VALID", "invalid");
        dataStore.getMultiKey("NON_VALID", "invalid");
        dataStore.get("NON_VALID");
        dataStore.getByValue("NON_VALID", "invalid");
        dataStore.remove("NON_VALID", "invalid");
        dataStore.remove("NON_VALID", "invalid", "invalid");
    }

}

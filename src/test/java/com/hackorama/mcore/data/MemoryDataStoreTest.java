package com.hackorama.mcore.data;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MemoryDataStoreTest {

    private DataStore dataStore;

    @Before
    public void setUp() throws Exception {
        if(dataStore == null) {
            dataStore = new MemoryDataStore();
        }
    }

    @After
    public void tearDown() throws Exception {
        dataStore.clear();
    }

    @Test
    public void datastore_insertedValues_matchesOnGettingByKey() {
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

    @Test
    public void datastore_insertedMultiKeyValues_matchesOnGettingByKey() {
        dataStore.putMultiKey("TABLE_ONE", "1_1", "one_one");
        dataStore.putMultiKey("TABLE_ONE", "1_2", "one_two");
        dataStore.putMultiKey("TABLE_ONE", "1_3", "one_three");
        dataStore.putMultiKey("TABLE_ONE", "1_4", "one_same");
        dataStore.putMultiKey("TABLE_ONE", "1_5", "one_same");
        dataStore.putMultiKey("TABLE_ONE", "1_6", "one_same");
        dataStore.putMultiKey("TABLE_ONE", "1_7", "one_seven");

        dataStore.putMultiKey("TABLE_TWO", "1_1", "two_one");
        dataStore.putMultiKey("TABLE_TWO", "1_2", "two_two");
        dataStore.putMultiKey("TABLE_TWO", "1_3", "two_three");
        dataStore.putMultiKey("TABLE_TWO", "1_0", "two_four");
        dataStore.putMultiKey("TABLE_TWO", "1_0", "two_five");
        dataStore.putMultiKey("TABLE_TWO", "1_0", "two_six");
        dataStore.putMultiKey("TABLE_TWO", "1_7", "two_seven");


        assertTrue(dataStore.contains("TABLE_ONE", "1_1"));
        assertFalse(dataStore.contains("TABLE_ONE", "false"));

        assertEquals("one_one", dataStore.getMultiKey("TABLE_ONE", "1_1").get(0));
        assertEquals("one_two", dataStore.getMultiKey("TABLE_ONE", "1_2").get(0));
        assertEquals("one_three", dataStore.getMultiKey("TABLE_ONE", "1_3").get(0));

        assertEquals("two_one", dataStore.getMultiKey("TABLE_TWO", "1_1").get(0));
        assertEquals("two_two", dataStore.getMultiKey("TABLE_TWO", "1_2").get(0));
        assertEquals("two_three", dataStore.getMultiKey("TABLE_TWO", "1_3").get(0));

        dataStore.remove("TABLE_ONE", "1_2");
        assertEquals("one_one", dataStore.getMultiKey("TABLE_ONE", "1_1").get(0));
        assertEquals(0, dataStore.getMultiKey("TABLE_ONE", "1_2").size());
        assertEquals("one_three", dataStore.getMultiKey("TABLE_ONE", "1_3").get(0));
        assertFalse(dataStore.contains("TABLE_ONE", "1_2"));

        assertEquals("two_one", dataStore.getMultiKey("TABLE_TWO", "1_1").get(0));
        assertEquals("two_two", dataStore.getMultiKey("TABLE_TWO", "1_2").get(0));
        assertEquals("two_three", dataStore.getMultiKey("TABLE_TWO", "1_3").get(0));

        assertEquals(3, dataStore.getByValue("TABLE_ONE", "one_same").size());
        assertTrue(dataStore.getByValue("TABLE_ONE", "one_same").contains("1_4"));
        assertTrue(dataStore.getByValue("TABLE_ONE", "one_same").contains("1_5"));
        assertTrue(dataStore.getByValue("TABLE_ONE", "one_same").contains("1_6"));

        assertEquals(0, dataStore.getByValue("TABLE_TWO", "two_same").size());
        assertEquals(3, dataStore.getMultiKey("TABLE_TWO", "1_0").size());
        assertTrue(dataStore.getMultiKey("TABLE_TWO", "1_0").contains("two_four"));
        assertTrue(dataStore.getMultiKey("TABLE_TWO", "1_0").contains("two_five"));
        assertTrue(dataStore.getMultiKey("TABLE_TWO", "1_0").contains("two_six"));
    }

}

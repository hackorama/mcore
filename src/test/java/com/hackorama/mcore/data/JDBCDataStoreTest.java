package com.hackorama.mcore.data;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hackorama.mcore.data.jdbc.JDBCDataStore;

public class JDBCDataStoreTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws SQLException {
        DataStore dataStore = new JDBCDataStore();
        dataStore.put("TEST", "ONE", "UNO");
        assertEquals("UNO", dataStore.get("TEST", "ONE"));
        dataStore.clear();
    }

}

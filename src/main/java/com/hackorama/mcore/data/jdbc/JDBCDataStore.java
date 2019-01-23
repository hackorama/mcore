package com.hackorama.mcore.data.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.hackorama.mcore.data.DataStore;

/**
 * DataStore implementation using JDBC
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class JDBCDataStore implements DataStore {

    private static final String DEFAULT_URL = "jdbc:h2:mem:";
    private static final String DEFAULT_DRIVER = "org.h2.Driver";
    private static final int DEFAULT_KEY_SIZE = 256;
    private static final int DEFAULT_VALUE_SIZE = 256;
    private Set<String> singleKeyStores = new HashSet<String>();
    private Set<String> multiKeyStores = new HashSet<String>();
    private QueryRunner queryRunner = new QueryRunner();
    private Connection conn;

    public JDBCDataStore() throws SQLException {
        this(DEFAULT_URL);
    }

    public JDBCDataStore(String url) throws SQLException {
        this(DEFAULT_URL, DEFAULT_DRIVER);
    }

    public JDBCDataStore(String url, String driver) throws SQLException {
        connect(url, driver);
    }

    public JDBCDataStore(String url, String driver, String user, String password) throws SQLException {
        connect(url, driver, user, password);
    }

    @Override
    public void clear() {
        try {
            DbUtils.close(conn);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO Custom exception and logging
        }
    }

    private void connect(String url, String driver) throws SQLException {
        DbUtils.loadDriver(driver);
        conn = DriverManager.getConnection(url);
    }

    private void connect(String url, String driver, String user, String password) throws SQLException {
        DbUtils.loadDriver(driver);
        conn = DriverManager.getConnection(url, user, password);
    }

    @Override
    public boolean contains(String store, String key) {
        if (!ifTableExists(store)) {
            return false; // TODO Debug log
        }
        String selectSQL = "SELECT key FROM " + store + " WHERE key = ?";
        List<Object[]> result = new ArrayList<>();
        try {
            result = queryRunner.query(conn, selectSQL, new ArrayListHandler(), key);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO Custom exception and logging
        }
        return !result.isEmpty();
    }

    private void createMultiTable(String table) {
        if (!singleKeyStores.contains(table)) {
            if (multiKeyStores.contains(table)) {
                throw new RuntimeException("Another store already exists with the same name " + table);
            }
            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + table + " " + "(key VARCHAR(" + DEFAULT_KEY_SIZE + "), "
                    + "value VARCHAR(" + DEFAULT_VALUE_SIZE + "))";
            singleKeyStores.add(table);
            try {
                queryRunner.execute(conn, createTableSQL);
            } catch (SQLException e) {
                e.printStackTrace(); // TODO Custom exception and logging
            }
        }
    }

    private void createTable(String table) {
        if (!multiKeyStores.contains(table)) {
            if (singleKeyStores.contains(table)) {
                throw new RuntimeException("Another store already exists with the same name " + table);
            }
            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + table + " " + "(key VARCHAR(" + DEFAULT_KEY_SIZE + "), "
                    + "value VARCHAR(" + DEFAULT_VALUE_SIZE + "), PRIMARY KEY (key)) ";
            multiKeyStores.add(table);
            try {
                queryRunner.execute(conn, createTableSQL);
            } catch (SQLException e) {
                e.printStackTrace(); // TODO Custom exception and logging
            }
        }
    }

    @Override
    public List<String> get(String store) {
        return getColumnValues(store, "value");
    }

    @Override
    public String get(String store, String key) {
        String result = null;
        if (!ifTableExists(store)) {
            return result; // TODO Debug log
        }
        String selectSQL = "SELECT value FROM " + store + " WHERE key = ?";
        try {
            result = queryRunner.query(conn, selectSQL, new ScalarHandler<String>(), key);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO Custom exception and logging
        }
        return result;
    }

    @Override
    public List<String> getByValue(String store, String value) {
        List<String> resultList = new ArrayList<>();
        if (!ifTableExists(store)) {
            return resultList; // TODO Debug log
        }
        String selectSQL = "SELECT key FROM " + store + " WHERE value = ?";
        try {
            resultList = queryRunner.query(conn, selectSQL, new ColumnListHandler<String>(), value);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO Custom exception and logging
        }
        return resultList;
    }

    private List<String> getColumnValues(String store, String column) {
        List<String> resultList = new ArrayList<>();
        if (!ifTableExists(store)) {
            return resultList; // TODO Debug log
        }
        String selectSQL = "SELECT " + column + " FROM " + store;
        try {
            resultList = queryRunner.query(conn, selectSQL, new ColumnListHandler<String>());
        } catch (SQLException e) {
            e.printStackTrace(); // TODO Custom exception and logging
        }
        return resultList;
    }

    @Override
    public Set<String> getKeys(String store) {
        return new HashSet<String>(getColumnValues(store, "key"));
    }

    @Override
    public List<String> getMultiKey(String store, String key) {
        List<String> resultList = new ArrayList<>();
        if (!ifTableExists(store)) {
            return resultList; // TODO Debug log
        }
        String selectSQL = "SELECT value FROM " + store + " WHERE key = ?";
        try {
            resultList = queryRunner.query(conn, selectSQL, new ColumnListHandler<String>(), key);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO Custom exception and logging
        }
        return resultList;
    }

    private boolean ifTableExists(String table) {
        return singleKeyStores.contains(table) || multiKeyStores.contains(table); // TODO Check DB
    }

    private void replace(String store, String key, String value) {
        String insertSQL = "REPLACE INTO " + store + " " + "VALUES (?, ?)";
        try {
            queryRunner.execute(conn, insertSQL, key, value);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO Custom exception and logging
        }
    }

    private void insert(String store, String key, String value) {
        String insertSQL = "INSERT INTO " + store + " " + "VALUES (?, ?)";
        try {
            queryRunner.execute(conn, insertSQL, key, value);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO Custom exception and logging
        }
    }

    @Override
    public void put(String store, String key, String value) {
        createTable(store);
        replace(store, key, value);
    }

    @Override
    public void putMultiKey(String store, String key, String value) {
        createMultiTable(store);
        insert(store, key, value);
    }

    @Override
    public void remove(String store, String key) {
        if (!ifTableExists(store)) {
            return; // TODO Debug log
        }
        String deleteSQL = "DELETE from " + store + " WHERE key = ?";
        try {
            queryRunner.execute(conn, deleteSQL, key);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO Custom exception and logging
        }
    }

    @Override
    public void remove(String store, String key, String value) {
        if (!ifTableExists(store)) {
            return; // TODO Debug log
        }
        String deleteSQL = "DELETE from " + store + " WHERE key = ? AND value = ?";
        try {
            queryRunner.execute(conn, deleteSQL, key, value);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO Custom exception and logging
        }
    }

}

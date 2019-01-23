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
    private Set<String> tableNames = new HashSet<String>();
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
        // TODO Auto-generated method stub
        return false;
    }

    private void createTable(String table) {
        if (!tableNames.contains(table)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + table + " " + "(key VARCHAR(255), "
                    + "value VARCHAR(255), PRIMARY KEY (key)) ";
            tableNames.add(table);
            try {
                queryRunner.execute(conn, createTableSQL);
            } catch (SQLException e) {
                e.printStackTrace(); // TODO Custom exception and logging
            }
        }
    }

    @Override
    public List<String> get(String store) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String get(String store, String key) {
        createTable(store);
        String selectSQL = "SELECT value FROM " + store + " WHERE key = ?";
        List<Object[]> result = new ArrayList<>();
        ;
        try {
            result = queryRunner.query(conn, selectSQL, new ArrayListHandler(), key);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO Custom exception and logging
        }
        return result.isEmpty() || result.get(0).length == 0 ? null : result.get(0)[0].toString();
    }

    @Override
    public List<String> getByValue(String store, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getKeys(String store) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getMultiKey(String store, String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void put(String store, String key, String value) {
        createTable(store);
        String insertSQL = "INSERT INTO " + store + " " + "VALUES (?, ?)";
        try {
            queryRunner.execute(conn, insertSQL, key, value);
        } catch (SQLException e) {
            e.printStackTrace(); // TODO Custom exception and logging
        }
    }

    @Override
    public void putMultiKey(String store, String key, String value) {
        // TODO Auto-generated method stub
    }

    @Override
    public void remove(String store, String key) {
        // TODO Auto-generated method stub
    }

    @Override
    public void remove(String store, String key, String value) {
        // TODO Auto-generated method stub
    }

}

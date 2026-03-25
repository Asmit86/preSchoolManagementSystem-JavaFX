package com.preschool.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/preschoolFinalDB";
    private static final String USER = "root";
    private static final String PASSWORD = "Divyana@2021";

    /**
     * Returns a NEW Connection each call.
     * Each DAO uses try-with-resources which calls conn.close() — this
     * is safe because every call gets its own fresh connection.
     * (The original code returned a shared static Connection, which
     *  caused all DAOs to fail after the first try-with-resources block
     *  closed the shared instance.)
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

package com.preschool.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Utility class for database connection
 */
public class DatabaseUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/preschoolSystemDB";
    private static final String USER = "root";
    private static final String PASSWORD = "Divyana@2021";

    /**
     * Returns a NEW Connection each call.
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

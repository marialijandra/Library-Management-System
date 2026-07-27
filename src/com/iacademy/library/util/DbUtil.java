package com.iacademy.library.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {

    // 1. Change "database_name" to the exact name of your database in phpMyAdmin (e.g., library_db)
    private static final String URL = "jdbc:mysql://localhost:3306/database_schema";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // XAMPP default is blank

    static {
        try {
            // Explicitly load the MySQL Driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Ensure the connector JAR is in your WEB-INF/lib/ directory.");
            e.printStackTrace();
        }
    }

    /**
     * Direct connection to local XAMPP MySQL database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Closes AutoCloseable database resources (Connection, Statement, ResultSet) quietly.
     */
    public static void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    // Quiet close
                }
            }
        }
    }
}



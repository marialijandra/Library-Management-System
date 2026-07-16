package com.iacademy.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central place to grab a JDBC connection.
 *
 * IMPORTANT (team note): the project only ships mysql-connector-j-8.0.33.jar
 * in WebContent/WEB-INF/lib, so this class (and the whole Transactions module)
 * is written against MySQL. The database_schema.sql that was already in the
 * repo uses PostgreSQL-only syntax (UUID type, gen_random_uuid()). Since there
 * is no Postgres driver bundled, I added database/database_schema_mysql.sql,
 * which is the same schema translated to MySQL. If the team decides to use
 * Postgres instead, swap the driver class name/URL below and add the
 * postgresql jar to WEB-INF/lib - the DAO code itself doesn't care which
 * database it talks to.
 *
 * EDIT the constants below to match your local MySQL setup.
 */
public class DBUtil {

    private static final String URL =
            "jdbc:mysql://localhost:3306/iacademy_library?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found on classpath.", e);
        }
    }

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }
}

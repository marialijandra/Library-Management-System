package com.iacademy.library.service;

import com.iacademy.library.model.Role;
import com.iacademy.library.util.DbUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    public boolean registerUser(String firstName, String surname, String email, String password, Role targetRole, Role creatorRole) {
        // Enforce role authorization (e.g., only admins can create other admins)
        if (targetRole == Role.ADMIN && creatorRole != Role.ADMIN) {
            return false;
        }

        String sql = "INSERT INTO users (first_name, surname, email, password, role) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DbUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, firstName);
            stmt.setString(2, surname);
            stmt.setString(3, email);
            stmt.setString(4, password); // Ideally, use a password hashing library (e.g., BCrypt) here
            stmt.setString(5, targetRole.name());
            // Convert the Java Enum (e.g., STUDENT) to lowercase (e.g., student)
            // to match the MySQL ENUM structure
            stmt.setString(5, targetRole.name().toLowerCase());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Log error to console or logging framework
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.closeQuietly(stmt, conn);
        }
    }
    // Add this method inside the UserService class:
    public Role login(String email, String password) throws SQLException {
        String sql = "SELECT password, role FROM users WHERE email = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");
                String dbRoleStr = rs.getString("role");

                // Check if password matches (Plain text check for development)
                if (dbPassword.equals(password)) {
                    // Convert DB lowercase role string to Java uppercase Role Enum
                    return Role.valueOf(dbRoleStr.toUpperCase());
                }
            }
            // Returns null if user is not in the system or password is incorrect
            return null;
        } finally {
            DbUtil.closeQuietly(rs, stmt, conn);
        }
    }


}

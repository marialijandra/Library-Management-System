package com.iacademy.library.service;

import com.iacademy.library.model.Role;
import com.iacademy.library.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserService {

    public boolean registerUser(String firstName, String surname, String email, String password, Role targetRole, Role creatorRole) {
        // Enforce role authorization (e.g., only admins can create other admins)
        if (targetRole == Role.ADMIN && creatorRole != Role.ADMIN) {
            return false;
        }

        String sql = "INSERT INTO users (id, first_name, surname, email, password, role) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, firstName);
            stmt.setString(3, surname);
            stmt.setString(4, email);
            stmt.setString(5, password); // Note: For production use BCrypt/PBKDF2 hashing
            stmt.setString(6, targetRole.name().toLowerCase());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Role login(String email, String password) throws SQLException {
        String sql = "SELECT password, role FROM users WHERE email = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("password");
                    String dbRoleStr = rs.getString("role");

                    // Check if password matches
                    if (dbPassword.equals(password)) {
                        // Convert DB lowercase role string to Java uppercase Role Enum
                        return Role.valueOf(dbRoleStr.toUpperCase());
                    }
                }
            }
        }
        return null;
    }
}

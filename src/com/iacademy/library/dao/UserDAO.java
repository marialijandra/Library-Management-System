package com.iacademy.library.dao;

import com.iacademy.library.model.User;
import com.iacademy.library.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Read-only access to the `users` table for the Transactions module.
 * Registering/editing users is the Admin module's responsibility - this DAO
 * only ever needs to check "is this email a registered user, and who are they".
 */
public class UserDAO {

    public User findByEmail(String email) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            return findByEmail(conn, email);
        }
    }

    public User findByEmail(Connection conn, String email) throws SQLException {
        String sql = "SELECT id, first_name, surname, email, password, role FROM users WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getString("id"));
        u.setFirstName(rs.getString("first_name"));
        u.setSurname(rs.getString("surname"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        return u;
    }
}

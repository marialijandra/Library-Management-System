package com.iacademy.library.dao;

import com.iacademy.library.model.Book;
import com.iacademy.library.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Access to the `books` table needed by the Transactions module: listing books
 * for the "select a book" dropdown, and incrementing/decrementing quantity
 * whenever a copy is borrowed or returned. Full CRUD (add/edit/delete a title)
 * belongs to the Book Catalog module - this DAO does not implement it.
 */
public class BookDAO {

    public List<Book> getAllBooks() throws SQLException {
        String sql = "SELECT id, title, description, quantity, image_url FROM books ORDER BY title";
        List<Book> books = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                books.add(mapRow(rs));
            }
        }
        return books;
    }

    public Book getById(String id) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            return getById(conn, id, false);
        }
    }

    /**
     * Fetches a book within an existing (transactional) connection.
     * @param forUpdate locks the row (SELECT ... FOR UPDATE) so quantity checks
     *                   stay correct if two librarians act at the same time.
     */
    public Book getById(Connection conn, String id, boolean forUpdate) throws SQLException {
        String sql = "SELECT id, title, description, quantity, image_url FROM books WHERE id = ?"
                + (forUpdate ? " FOR UPDATE" : "");
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public void updateQuantity(Connection conn, String bookId, int newQuantity) throws SQLException {
        String sql = "UPDATE books SET quantity = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setString(2, bookId);
            ps.executeUpdate();
        }
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setId(rs.getString("id"));
        b.setTitle(rs.getString("title"));
        b.setDescription(rs.getString("description"));
        b.setQuantity(rs.getInt("quantity"));
        b.setImageUrl(rs.getString("image_url"));
        return b;
    }
}

package com.iacademy.library.dao;

import com.iacademy.library.model.Book;
import com.iacademy.library.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Access to the `books` table needed by the Transactions module: listing books
 * for the "select a book" dropdown, and incrementing/decrementing quantity
 * whenever a copy is borrowed or returned. Full CRUD (add/edit/delete a title)
 * belongs to the Book Catalog module - this DAO does not implement it.
 */
public class BookDAO {
    protected Connection getConnection() throws SQLException {
        return DBUtil.getConnection();
    }

    // CREATE
    public boolean insertBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, description, quantity, image_url) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getDescription());
            preparedStatement.setInt(3, book.getQuantity());
            preparedStatement.setString(4, book.getImageUrl());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            // Catches the UNIQUE constraint violation on the title
            throw new SQLException("A book with this title already exists.");
        }
    }

    // READ ALL
    public List<Book> selectAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title ASC";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                int quantity = rs.getInt("quantity");
                String imageUrl = rs.getString("image_url");
                books.add(new Book(String.valueOf(id), title, description, quantity, imageUrl));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> getAllBooks() {
        return selectAllBooks();
    }

    // READ ONE (For editing)
    public Book selectBook(int id) {
        Book book = null;
        String sql = "SELECT * FROM books WHERE id =?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                String description = rs.getString("description");
                int quantity = rs.getInt("quantity");
                String imageUrl = rs.getString("image_url");
                book = new Book(String.valueOf(id), title, description, quantity, imageUrl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }

    public Book getById(Connection conn, String id, boolean forUpdate) throws SQLException {
        Book book = null;
        String sql = "SELECT id, title, description, quantity, image_url FROM books WHERE id = ?"
                + (forUpdate ? " FOR UPDATE" : "");
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    book = mapRow(rs);
                }
            }
        }
        return book;
    }

    // UPDATE
    public boolean updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, description = ?, quantity = ?, image_url = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getDescription());
            statement.setInt(3, book.getQuantity());
            statement.setString(4, book.getImageUrl());
            statement.setInt(5, Integer.parseInt(book.getId()));

            return statement.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            // Catches the UNIQUE constraint violation on the title during updates
            throw new SQLException("A book with this title already exists.");
        }
    }

    public void updateQuantity(Connection conn, String id, int newQuantity) throws SQLException {
        String sql = "UPDATE books SET quantity = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, Integer.parseInt(id));
            ps.executeUpdate();
        }
    }

    // DELETE
    public boolean deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setId(String.valueOf(rs.getInt("id")));
        b.setTitle(rs.getString("title"));
        b.setDescription(rs.getString("description"));
        b.setQuantity(rs.getInt("quantity"));
        b.setImageUrl(rs.getString("image_url"));
        return b;
    }
}

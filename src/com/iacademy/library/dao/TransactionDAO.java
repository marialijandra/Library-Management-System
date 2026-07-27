package com.iacademy.library.dao;

import com.iacademy.library.model.Book;
import com.iacademy.library.model.Borrower;
import com.iacademy.library.model.BorrowerLoan;
import com.iacademy.library.model.Transaction;
import com.iacademy.library.model.User;
import com.iacademy.library.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * All CRUD logic for the Transaction ("Borrowers") tab of the Library Manager.
 *
 * Business rules enforced here (per the spec):
 *  - Books with 0 quantity can't be borrowed.
 *  - A borrower can't hold two active ("borrowed") copies of the same title
 *    at once, but re-borrowing a title they already returned just flips the
 *    existing record back to "borrowed" instead of creating a duplicate row.
 *  - Adding a book to a borrower who's already in the list just appends to
 *    their existing transaction history (handled naturally, since we always
 *    look the user up by email first).
 *  - Any registered user (student/librarian/admin) can be a borrower - this
 *    DAO validates the email against the shared `users` table rather than
 *    creating a standalone "borrower" record.
 */
public class TransactionDAO {

    private final BookDAO bookDAO = new BookDAO();
    private final UserDAO userDAO = new UserDAO();

    // ---------------------------------------------------------------- READ

    public List<Borrower> getAllBorrowers() throws SQLException {
        String sql =
                "SELECT t.id AS transaction_id, t.status, t.user_id, t.book_id, " +
                "       u.first_name, u.surname, u.email, b.title AS book_title " +
                "FROM transactions t " +
                "JOIN users u ON t.user_id = u.id " +
                "JOIN books b ON t.book_id = b.id " +
                "ORDER BY u.surname, u.first_name, b.title";

        Map<String, Borrower> byUser = new LinkedHashMap<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String userId = rs.getString("user_id");
                Borrower borrower = byUser.get(userId);
                if (borrower == null) {
                    borrower = new Borrower(userId, rs.getString("first_name"),
                            rs.getString("surname"), rs.getString("email"));
                    byUser.put(userId, borrower);
                }
                borrower.addLoan(new BorrowerLoan(
                        rs.getString("transaction_id"),
                        rs.getString("book_id"),
                        rs.getString("book_title"),
                        rs.getString("status")));
            }
        }

        return new ArrayList<>(byUser.values());
    }

    // -------------------------------------------------------------- CREATE

    /**
     * Registers (or adds to) a borrower's history for a set of books.
     * Returns a list of human-readable warnings for any books that were
     * skipped (already borrowed, or out of copies); everything else is
     * applied.
     */
    public List<String> addBorrower(String firstName, String surname, String email, List<String> bookIds)
            throws SQLException {

        List<String> messages = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                User user = userDAO.findByEmail(conn, email);
                if (user == null) {
                    throw new IllegalArgumentException(
                            "No registered user was found with email \"" + email +
                            "\". Only registered users (student, librarian, or admin) can be borrowers - " +
                            "please register them first.");
                }

                for (String bookId : bookIds) {
                    if (bookId == null || bookId.isEmpty()) continue;
                    String warning = borrowOneBook(conn, user.getId(), bookId);
                    if (warning != null) messages.add(warning);
                }

                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }

        return messages;
    }

    // -------------------------------------------------------------- UPDATE

    /**
     * Updates existing loan statuses (toggle Still Borrowed / Returned) and/or
     * appends brand-new book loans to an existing borrower, all in one
     * transaction. Returns warnings for anything skipped.
     */
    public List<String> updateBorrower(String userId, Map<String, String> loanStatusUpdates,
                                        List<String> newBookIds) throws SQLException {

        List<String> messages = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (loanStatusUpdates != null) {
                    for (Map.Entry<String, String> entry : loanStatusUpdates.entrySet()) {
                        String warning = applyStatusChange(conn, entry.getKey(), entry.getValue());
                        if (warning != null) messages.add(warning);
                    }
                }

                if (newBookIds != null) {
                    for (String bookId : newBookIds) {
                        if (bookId == null || bookId.isEmpty()) continue;
                        String warning = borrowOneBook(conn, userId, bookId);
                        if (warning != null) messages.add(warning);
                    }
                }

                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }

        return messages;
    }

    // -------------------------------------------------------------- DELETE

    /** Deletes a single loan record. If it was still "borrowed", the copy is returned to stock first. */
    public void deleteTransaction(String transactionId) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Transaction t = getTransactionById(conn, transactionId, true);
                if (t == null) {
                    throw new IllegalArgumentException("That transaction no longer exists.");
                }
                if (t.isBorrowed()) {
                    Book book = bookDAO.getById(conn, t.getBookId(), true);
                    if (book != null) {
                        bookDAO.updateQuantity(conn, book.getId(), book.getQuantity() + 1);
                    }
                }
                deleteById(conn, transactionId);
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // ------------------------------------------------------------ HELPERS

    /**
     * Creates or revives a "borrowed" record for (userId, bookId), decrementing
     * stock as needed. Returns a warning message if it could not be done
     * (already borrowed / no copies left), or null on success.
     */
    private String borrowOneBook(Connection conn, String userId, String bookId) throws SQLException {
        Book book = bookDAO.getById(conn, bookId, true);
        if (book == null) {
            return "A selected book could not be found.";
        }

        Transaction existing = findTransactionRow(conn, userId, bookId, true);

        if (existing != null && existing.isBorrowed()) {
            return "\"" + book.getTitle() + "\" is already borrowed by this user.";
        }

        if (book.getQuantity() <= 0) {
            return "\"" + book.getTitle() + "\": All copies are currently borrowed.";
        }

        if (existing != null) {
            // They returned it before - flip the same record back to borrowed
            // instead of creating a duplicate row.
            updateStatus(conn, existing.getId(), "borrowed");
        } else {
            insertTransaction(conn, userId, bookId);
        }

        bookDAO.updateQuantity(conn, book.getId(), book.getQuantity() - 1);
        return null;
    }

    /** Applies a status change requested from the UI (Still Borrowed <-> Returned). */
    private String applyStatusChange(Connection conn, String transactionId, String newStatus) throws SQLException {
        Transaction t = getTransactionById(conn, transactionId, true);
        if (t == null) {
            return "A transaction being updated no longer exists.";
        }
        if (t.getStatus().equals(newStatus)) {
            return null; // no change
        }

        Book book = bookDAO.getById(conn, t.getBookId(), true);

        if ("returned".equals(newStatus)) {
            updateStatus(conn, t.getId(), "returned");
            if (book != null) {
                bookDAO.updateQuantity(conn, book.getId(), book.getQuantity() + 1);
            }
            return null;
        } else if ("borrowed".equals(newStatus)) {
            if (book == null || book.getQuantity() <= 0) {
                String title = (book != null) ? book.getTitle() : "This book";
                return "\"" + title + "\": All copies are currently borrowed.";
            }
            updateStatus(conn, t.getId(), "borrowed");
            bookDAO.updateQuantity(conn, book.getId(), book.getQuantity() - 1);
            return null;
        }

        return "Unrecognized status \"" + newStatus + "\".";
    }

    private Transaction findTransactionRow(Connection conn, String userId, String bookId, boolean forUpdate)
            throws SQLException {
        String sql = "SELECT id, user_id, book_id, status FROM transactions WHERE user_id = ? AND book_id = ?"
                + (forUpdate ? " FOR UPDATE" : "");
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Transaction(rs.getString("id"), rs.getString("user_id"),
                            rs.getString("book_id"), rs.getString("status"));
                }
            }
        }
        return null;
    }

    private Transaction getTransactionById(Connection conn, String id, boolean forUpdate) throws SQLException {
        String sql = "SELECT id, user_id, book_id, status FROM transactions WHERE id = ?"
                + (forUpdate ? " FOR UPDATE" : "");
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Transaction(rs.getString("id"), rs.getString("user_id"),
                            rs.getString("book_id"), rs.getString("status"));
                }
            }
        }
        return null;
    }

    private void insertTransaction(Connection conn, String userId, String bookId) throws SQLException {
        String sql = "INSERT INTO transactions (id, user_id, book_id, status) VALUES (?, ?, ?, 'borrowed')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, userId);
            ps.setString(3, bookId);
            ps.executeUpdate();
        }
    }

    private void updateStatus(Connection conn, String transactionId, String status) throws SQLException {
        String sql = "UPDATE transactions SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, transactionId);
            ps.executeUpdate();
        }
    }

    private void deleteById(Connection conn, String transactionId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transactionId);
            ps.executeUpdate();
        }
    }
}

package com.iacademy.library.model;

/** Mirrors a row in the `transactions` table. status is "borrowed" or "returned". */
public class Transaction {

    private String id;
    private String userId;
    private String bookId;
    private String status;

    public Transaction() {
    }

    public Transaction(String id, String userId, String bookId, String status) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isBorrowed() { return "borrowed".equals(status); }
    public boolean isReturned() { return "returned".equals(status); }
}

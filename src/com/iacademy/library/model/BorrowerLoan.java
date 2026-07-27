package com.iacademy.library.model;

/** One book loan entry shown inside a borrower's row/modal in the Transaction tab. */
public class BorrowerLoan {

    private String transactionId;
    private String bookId;
    private String bookTitle;
    private String status;

    public BorrowerLoan(String transactionId, String bookId, String bookTitle, String status) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.status = status;
    }

    public String getTransactionId() { return transactionId; }
    public String getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public String getStatus() { return status; }

    public String toJson() {
        return "{"
                + "\"transactionId\":" + com.iacademy.library.util.JsonUtil.quote(transactionId) + ","
                + "\"bookId\":" + com.iacademy.library.util.JsonUtil.quote(bookId) + ","
                + "\"bookTitle\":" + com.iacademy.library.util.JsonUtil.quote(bookTitle) + ","
                + "\"status\":" + com.iacademy.library.util.JsonUtil.quote(status)
                + "}";
    }
}

package com.iacademy.library.servlet;

import com.iacademy.library.dao.BookDAO;
import com.iacademy.library.dao.TransactionDAO;
import com.iacademy.library.model.Book;
import com.iacademy.library.model.Borrower;
import com.iacademy.library.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Backs the "Transaction" (Borrowers) tab of libraryManager.jsp.
 *
 * GET  ?action=list                  -> JSON array of borrowers + their loans
 * GET  ?action=books                 -> JSON array of books (id/title/quantity) for the "select a book" dropdowns
 * POST action=addBorrower            -> params: firstName, surname, email, bookIds (repeated)
 * POST action=updateBorrower         -> params: userId, transactionIds (repeated), statuses (repeated, same order
 *                                        as transactionIds), newBookIds (repeated, optional)
 * POST action=deleteLoan             -> params: transactionId
 *
 * Integration note for whoever builds LoginServlet: on a successful librarian
 * or admin login, please set session.setAttribute("role", user.getRole()) so
 * this servlet can confirm the caller is allowed to manage transactions.
 */
@WebServlet("/TransactionServlet")
public class TransactionServlet extends HttpServlet {

    /**
     * TEST MODE - for local development only, before LoginServlet exists.
     *
     * When true, the session/role check below is skipped entirely so you can
     * hit TransactionServlet directly (e.g. by just opening libraryManager.jsp
     * in a browser) without logging in first.
     *
     * !! SET THIS BACK TO false ONCE LOGIN IS WIRED UP !! Leaving it true in
     * a real deployment means anyone can add/delete transactions with no
     * authentication at all.
     */
    private static final boolean TEST_MODE = false;

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final BookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (!isAuthorized(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            writeJson(resp, "{\"error\":\"Please log in as a librarian or admin.\"}");
            return;
        }

        String action = req.getParameter("action");
        try {
            if ("books".equals(action)) {
                respondWithBooks(resp);
            } else {
                respondWithBorrowers(resp);
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(resp, "{\"error\":" + JsonUtil.quote("Database error: " + e.getMessage()) + "}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (!isAuthorized(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            writeJson(resp, "{\"success\":false,\"messages\":[\"Please log in as a librarian or admin.\"]}");
            return;
        }

        String action = req.getParameter("action");
        try {
            List<String> messages;

            if ("addBorrower".equals(action)) {
                String firstName = req.getParameter("firstName");
                String surname = req.getParameter("surname");
                String email = req.getParameter("email");
                List<String> bookIds = paramList(req, "bookIds");

                if (isBlank(firstName) || isBlank(surname) || isBlank(email) || bookIds.isEmpty()) {
                    respondResult(resp, false, listOf("Please fill out all fields and select at least one book."));
                    return;
                }

                messages = transactionDAO.addBorrower(firstName.trim(), surname.trim(), email.trim(), bookIds);
                respondResult(resp, true, messages);

            } else if ("updateBorrower".equals(action)) {
                String userId = req.getParameter("userId");
                List<String> transactionIds = paramList(req, "transactionIds");
                List<String> statuses = paramList(req, "statuses");
                List<String> newBookIds = paramList(req, "newBookIds");

                if (isBlank(userId)) {
                    respondResult(resp, false, listOf("Missing borrower reference."));
                    return;
                }

                Map<String, String> loanUpdates = new LinkedHashMap<>();
                int n = Math.min(transactionIds.size(), statuses.size());
                for (int i = 0; i < n; i++) {
                    loanUpdates.put(transactionIds.get(i), statuses.get(i));
                }

                messages = transactionDAO.updateBorrower(userId, loanUpdates, newBookIds);
                respondResult(resp, true, messages);

            } else if ("deleteLoan".equals(action)) {
                String transactionId = req.getParameter("transactionId");
                if (isBlank(transactionId)) {
                    respondResult(resp, false, listOf("Missing transaction id."));
                    return;
                }
                transactionDAO.deleteTransaction(transactionId);
                respondResult(resp, true, null);

            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                respondResult(resp, false, listOf("Unknown action."));
            }

        } catch (IllegalArgumentException e) {
            respondResult(resp, false, listOf(e.getMessage()));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            respondResult(resp, false, listOf("Database error: " + e.getMessage()));
        }
    }

    // ---------------------------------------------------------------- helpers

    private boolean isAuthorized(HttpServletRequest req) {
        if (TEST_MODE) {
            System.out.println("[TransactionServlet] TEST_MODE is ON - skipping login check. "
                    + "Set TEST_MODE = false once LoginServlet is wired up.");
            return true;
        }
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        Object role = session.getAttribute("role");
        return "librarian".equals(role) || "admin".equals(role);
    }

    private void respondWithBorrowers(HttpServletResponse resp) throws SQLException, IOException {
        List<Borrower> borrowers = transactionDAO.getAllBorrowers();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < borrowers.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(borrowers.get(i).toJson());
        }
        sb.append("]");
        writeJson(resp, sb.toString());
    }

    private void respondWithBooks(HttpServletResponse resp) throws SQLException, IOException {
        List<Book> books = bookDAO.getAllBooks();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < books.size(); i++) {
            if (i > 0) sb.append(",");
            Book b = books.get(i);
            sb.append("{")
              .append("\"id\":").append(JsonUtil.quote(b.getId())).append(",")
              .append("\"title\":").append(JsonUtil.quote(b.getTitle())).append(",")
              .append("\"description\":").append(JsonUtil.quote(b.getDescription())).append(",")
              .append("\"image\":").append(JsonUtil.quote(b.getImageUrl() == null ? "" : b.getImageUrl())).append(",")
              .append("\"quantity\":").append(b.getQuantity())
              .append("}");
        }
        sb.append("]");
        writeJson(resp, sb.toString());
    }

    private void respondResult(HttpServletResponse resp, boolean success, List<String> messages) throws IOException {
        String json = "{\"success\":" + success + ",\"messages\":" + JsonUtil.stringArray(messages) + "}";
        writeJson(resp, json);
    }

    private void writeJson(HttpServletResponse resp, String json) throws IOException {
        PrintWriter out = resp.getWriter();
        out.write(json);
        out.flush();
    }

    private List<String> paramList(HttpServletRequest req, String name) {
        String[] values = req.getParameterValues(name);
        List<String> list = new ArrayList<>();
        if (values != null) {
            for (String v : values) {
                if (v != null && !v.isEmpty()) list.add(v);
            }
        }
        return list;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private List<String> listOf(String s) {
        List<String> list = new ArrayList<>();
        list.add(s);
        return list;
    }
}

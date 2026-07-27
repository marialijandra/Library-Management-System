package com.iacademy.library.servlet;

import com.iacademy.library.dao.BookDAO;
import com.iacademy.library.model.Book;
import com.iacademy.library.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/books")
public class BookController extends HttpServlet {
    private BookDAO bookDAO;

    public void init() {
        bookDAO = new BookDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "insert":
                insertBook(request, response);
                break;
            case "delete":
                deleteBook(request, response);
                break;
            case "update":
                updateBook(request, response);
                break;
            default:
                // Redirect legacy/fallback views back to the main librarian manager page
                response.sendRedirect("views/libraryManager.jsp");
                break;
        }
    }

    private void insertBook(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            String imageUrl = request.getParameter("imageUrl");

            Book newBook = new Book(title, description, quantity, imageUrl);
            bookDAO.insertBook(newBook);
            JsonUtil.sendJsonResponse(response, true, "Book added successfully.", null);
        } catch (Exception e) {
            JsonUtil.sendJsonResponse(response, false, e.getMessage(), null);
        }
    }

    private void updateBook(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            String imageUrl = request.getParameter("imageUrl");

            Book book = new Book(String.valueOf(id), title, description, quantity, imageUrl);
            bookDAO.updateBook(book);
            JsonUtil.sendJsonResponse(response, true, "Book updated successfully.", null);
        } catch (Exception e) {
            JsonUtil.sendJsonResponse(response, false, e.getMessage(), null);
        }
    }

    private void deleteBook(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            bookDAO.deleteBook(id);
            JsonUtil.sendJsonResponse(response, true, "Book deleted successfully.", null);
        } catch (Exception e) {
            JsonUtil.sendJsonResponse(response, false, e.getMessage(), null);
        }
    }
}

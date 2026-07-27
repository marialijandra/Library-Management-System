package com.iacademy.library.servlet;

import com.iacademy.library.model.Role;
import com.iacademy.library.service.UserService;
import com.iacademy.library.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
    }

    /**
     * Serves the login page.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Pointing to login.jsp inside the views directory
        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }

    /**
     * Processes login requests.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            JsonUtil.sendJsonResponse(response, false, "Please enter both email and password.", null);
            return;
        }

        try {
            Role role = userService.login(email, password);

            if (role == null) {
                JsonUtil.sendJsonResponse(response, false, "This user is not registered in the system or password was incorrect.", null);
                return;
            }

            if (role == Role.STUDENT) {
                JsonUtil.sendJsonResponse(response, false, "Access Denied: Students are not permitted to access the management portal.", null);

            } else if (role == Role.ADMIN) {
                HttpSession session = request.getSession(true);
                session.setAttribute("role", Role.ADMIN);
                session.setAttribute("email", email);

                // Redirects to RegisterServlet mapping (which points to views/adminRegisterUser.jsp)
                JsonUtil.sendJsonResponse(response, true, null, "views/adminRegisterUser.jsp");

            } else if (role == Role.LIBRARIAN) {
                HttpSession session = request.getSession(true);
                session.setAttribute("role", Role.LIBRARIAN);
                session.setAttribute("email", email);

                // Redirects directly to libraryManager.jsp inside the views directory
                JsonUtil.sendJsonResponse(response, true, null, "views/libraryManager.jsp");
            }

        } catch (SQLException e) {
            JsonUtil.sendJsonResponse(response, false, "Database Error: " + e.getMessage(), null);
        }
    }
}
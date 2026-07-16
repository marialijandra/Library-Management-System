package com.iacademy.library.servlet;

import com.iacademy.library.service.UserService;
import com.iacademy.library.model.Role;
import com.iacademy.library.util.JsonUtil;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
    }

    /**
     * Serves the adminRegisterUser.jsp form inside the views directory.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Pointing to adminRegisterUser.jsp inside the views directory
        request.getRequestDispatcher("/views/adminRegisterUser.jsp").forward(request, response);
    }

    /**
     * Handles registration execution.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String firstName = request.getParameter("firstName");
        String surname = request.getParameter("surname");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String targetRoleStr = request.getParameter("role");

        Role targetRole = null;
        try {
            if (targetRoleStr != null) {
                targetRole = Role.valueOf(targetRoleStr.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            JsonUtil.sendJsonResponse(response, false, "Invalid role type selected.", null);
            return;
        }

        // Determine the creator role based on session (default to ADMIN if none found for testing/setup)
        Role creatorRole = Role.ADMIN;
        Object sessionRoleObj = request.getSession().getAttribute("role");
        if (sessionRoleObj instanceof String) {
            try {
                creatorRole = Role.valueOf(((String) sessionRoleObj).toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore and fall back to default
            }
        }

        if (firstName == null || surname == null || email == null || password == null || targetRole == null) {
            JsonUtil.sendJsonResponse(response, false, "Missing form data parameters.", null);
            return;
        }

        // Executes registration logic
        boolean isSuccess = userService.registerUser(
                firstName,
                surname,
                email,
                password,
                targetRole,
                creatorRole
        );

        if (isSuccess) {
            JsonUtil.sendJsonResponse(response, true, null, "register");
        } else {
            JsonUtil.sendJsonResponse(
                    response,
                    false,
                    "Registration rejected. Check for duplicate emails or database connectivity errors.",
                    null
            );
        }
    }
}

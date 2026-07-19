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
import javax.servlet.http.HttpSession;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
    }

    /**
     * Serves the adminRegisterUser.jsp form inside the views directory -
     * only to an already-logged-in admin. (adminRegisterUser.jsp also checks
     * this itself if opened directly, but checking here too means a stray
     * direct hit on /register can't skip it.)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
            return;
        }
        request.getRequestDispatcher("/views/adminRegisterUser.jsp").forward(request, response);
    }

    /**
     * Handles registration execution. Only an admin who is actually logged in
     * (session role == "admin") may register anyone, of any role - there is
     * no "no session yet" fallback. The very first admin account must come
     * from the seed data in database_schema.sql, not from this endpoint.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            JsonUtil.sendJsonResponse(response, false,
                    "You must be logged in as an admin to register new users.", null);
            return;
        }

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

        if (firstName == null || surname == null || email == null || password == null || targetRole == null) {
            JsonUtil.sendJsonResponse(response, false, "Missing form data parameters.", null);
            return;
        }

        // Executes registration logic - creatorRole is always ADMIN here since
        // isAdmin(request) already confirmed that above.
        boolean isSuccess = userService.registerUser(
                firstName,
                surname,
                email,
                password,
                targetRole,
                Role.ADMIN
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

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && "admin".equals(session.getAttribute("role"));
    }
}

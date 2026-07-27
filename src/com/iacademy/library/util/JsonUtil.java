package com.iacademy.library.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JsonUtil {

    /**
     * Sends a standardized JSON response to the client.
     */
    public static void sendJsonResponse(HttpServletResponse response, boolean success, String message, String redirectUrl)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"success\":").append(success);

        if (message != null) {
            json.append(",\"message\":\"").append(escapeJson(message)).append("\"");
        }
        if (redirectUrl != null) {
            json.append(",\"redirect\":\"").append(escapeJson(redirectUrl)).append("\"");
        }
        json.append("}");

        try (PrintWriter out = response.getWriter()) {
            out.print(json.toString());
            out.flush();
        }
    }

    /**
     * Escapes special characters to prevent invalid JSON syntax.
     */
    private static String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

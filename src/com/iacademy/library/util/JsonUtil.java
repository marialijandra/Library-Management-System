package com.iacademy.library.util;

import java.util.List;

/**
 * Tiny JSON writing helper. There's no Gson/Jackson jar bundled with the
 * project, so the Transactions module builds JSON strings by hand. Keep it
 * simple - this only ever needs to serialize flat DTOs, never arbitrary
 * objects.
 */
public final class JsonUtil {

    private JsonUtil() {
    }

    /** Escapes a string so it is safe to embed inside a JSON string literal. */
    public static String esc(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"':  sb.append("\\\""); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    public static String quote(String s) {
        return "\"" + esc(s) + "\"";
    }

    /** Builds a JSON array of string values, e.g. ["a","b"]. */
    public static String stringArray(List<String> values) {
        StringBuilder sb = new StringBuilder("[");
        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(quote(values.get(i)));
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /** Sends a standardized JSON response to the client. */
    public static void sendJsonResponse(javax.servlet.http.HttpServletResponse response, boolean success, String message, String redirectUrl)
            throws java.io.IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"success\":").append(success);

        if (message != null) {
            json.append(",\"message\":\"").append(esc(message)).append("\"");
        }
        if (redirectUrl != null) {
            json.append(",\"redirect\":\"").append(esc(redirectUrl)).append("\"");
        }
        json.append("}");

        try (java.io.PrintWriter out = response.getWriter()) {
            out.print(json.toString());
            out.flush();
        }
    }
}

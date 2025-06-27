import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageHandlers {

    private static final String ADMIN_TOKEN = "admin-secret-token";

    /**
     * A simple authorization check. In a real app, use a robust auth system.
     */
    private static boolean isAdmin(HttpRequest request) {
        String token = request.headers.get("X-Admin-Token");
        return ADMIN_TOKEN.equals(token);
    }

    /**
     * Handles creation of a new contact message.
     * Publicly accessible.
     */
    public static void handleCreateMessage(HttpRequest request, HttpResponse response) throws IOException {
        try {
            String name = extractJsonValue(request.body, "name");
            String email = extractJsonValue(request.body, "email");
            String message = extractJsonValue(request.body, "message");

            if (name == null || email == null || message == null) {
                response.sendJson(400, "Bad Request", "{\"error\":\"Name, email, and message are required\"}");
                return;
            }

            try (Connection conn = DatabaseManager.getInstance().getConnection()) {
                String sql = "INSERT INTO contact_messages (name, email, message) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, name);
                    stmt.setString(2, email);
                    stmt.setString(3, message);
                    stmt.executeUpdate();
                    System.out.println("New contact message from " + email + " saved.");
                }
            } catch (SQLException e) {
                System.err.println("Database error on message creation: " + e.getMessage());
                response.sendJson(500, "Internal Server Error", "{\"error\":\"Could not save message\"}");
                return;
            }

            // Send email notification
            try {
                String subject = "New Contact Form Submission";
                String body = "You have received a new contact form submission:\n\n" +
                    "Name: " + name + "\n" +
                    "Email: " + email + "\n" +
                    "Message: " + message;
                EmailSender.sendEmail("shreyashshinde751@gmail.com", subject, body);
            } catch (Exception e) {
                System.err.println("Failed to send email notification: " + e.getMessage());
            }

            response.sendJson(201, "Created", "{\"success\":true,\"message\":\"Message received\"}");
        } catch (Exception e) {
            responseSafeError(response, "Error creating message: " + e.getMessage());
        }
    }

    /**
     * Handles fetching all contact messages.
     * Admin access required.
     */
    public static void handleGetAllMessages(HttpRequest request, HttpResponse response) throws IOException {
        if (!isAdmin(request)) {
            response.sendJson(403, "Forbidden", "{\"error\":\"Admin access required\"}");
            return;
        }

        List<String> messagesJson = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "SELECT id, name, email, message, created_at FROM contact_messages ORDER BY created_at DESC";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    messagesJson.add(String.format(
                        "{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"message\":\"%s\",\"created_at\":\"%s\"}",
                        rs.getInt("id"),
                        escapeJson(rs.getString("name")),
                        escapeJson(rs.getString("email")),
                        escapeJson(rs.getString("message")),
                        rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            responseSafeError(response, "Database error retrieving messages: " + e.getMessage());
            return;
        }

        String jsonArray = "[" + String.join(",", messagesJson) + "]";
        response.sendJson(200, "OK", jsonArray);
    }

    /**
     * Handles fetching a single message by its ID.
     * Admin access required.
     */
    public static void handleGetMessage(HttpRequest request, HttpResponse response) throws IOException {
        if (!isAdmin(request)) {
            response.sendJson(403, "Forbidden", "{\"error\":\"Admin access required\"}");
            return;
        }

        try {
            int messageId = Integer.parseInt(request.params.get("id"));
            
            try (Connection conn = DatabaseManager.getInstance().getConnection()) {
                String sql = "SELECT id, name, email, message, created_at FROM contact_messages WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, messageId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String messageJson = String.format(
                                "{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"message\":\"%s\",\"created_at\":\"%s\"}",
                                rs.getInt("id"),
                                escapeJson(rs.getString("name")),
                                escapeJson(rs.getString("email")),
                                escapeJson(rs.getString("message")),
                                rs.getTimestamp("created_at")
                            );
                            response.sendJson(200, "OK", messageJson);
                        } else {
                            response.sendJson(404, "Not Found", "{\"error\":\"Message not found\"}");
                        }
                    }
                }
            } catch (SQLException e) {
                responseSafeError(response, "Database error getting message: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            response.sendJson(400, "Bad Request", "{\"error\":\"Invalid message ID\"}");
        } catch (Exception e) {
            responseSafeError(response, "Error getting message: " + e.getMessage());
        }
    }

    /**
     * Handles updating a message by its ID.
     * Admin access required.
     */
    public static void handleUpdateMessage(HttpRequest request, HttpResponse response) throws IOException {
        if (!isAdmin(request)) {
            response.sendJson(403, "Forbidden", "{\"error\":\"Admin access required\"}");
            return;
        }

        try {
            int messageId = Integer.parseInt(request.params.get("id"));
            String name = extractJsonValue(request.body, "name");
            String email = extractJsonValue(request.body, "email");
            String message = extractJsonValue(request.body, "message");

            if (name == null || email == null || message == null) {
                response.sendJson(400, "Bad Request", "{\"error\":\"Name, email, and message are required for update\"}");
                return;
            }

            try (Connection conn = DatabaseManager.getInstance().getConnection()) {
                String sql = "UPDATE contact_messages SET name = ?, email = ?, message = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, name);
                    stmt.setString(2, email);
                    stmt.setString(3, message);
                    stmt.setInt(4, messageId);
                    int updated = stmt.executeUpdate();
                    if (updated > 0) {
                        response.sendJson(200, "OK", "{\"success\":true,\"message\":\"Message updated\"}");
                    } else {
                        response.sendJson(404, "Not Found", "{\"error\":\"Message not found\"}");
                    }
                }
            } catch (SQLException e) {
                responseSafeError(response, "Database error updating message: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            response.sendJson(400, "Bad Request", "{\"error\":\"Invalid message ID\"}");
        } catch (Exception e) {
            responseSafeError(response, "Error updating message: " + e.getMessage());
        }
    }

    /**
     * Handles deleting a message by its ID.
     * Admin access required.
     */
    public static void handleDeleteMessage(HttpRequest request, HttpResponse response) throws IOException {
        if (!isAdmin(request)) {
            response.sendJson(403, "Forbidden", "{\"error\":\"Admin access required\"}");
            return;
        }

        try {
            int messageId = Integer.parseInt(request.params.get("id"));

            try (Connection conn = DatabaseManager.getInstance().getConnection()) {
                String sql = "DELETE FROM contact_messages WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, messageId);
                    int deleted = stmt.executeUpdate();
                    if (deleted > 0) {
                        response.sendJson(200, "OK", "{\"success\":true,\"message\":\"Message deleted\"}");
                    } else {
                        response.sendJson(404, "Not Found", "{\"error\":\"Message not found\"}");
                    }
                }
            } catch (SQLException e) {
                responseSafeError(response, "Database error deleting message: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            response.sendJson(400, "Bad Request", "{\"error\":\"Invalid message ID\"}");
        } catch (Exception e) {
            responseSafeError(response, "Error deleting message: " + e.getMessage());
        }
    }

    // Minimal JSON value extractor
    private static String extractJsonValue(String json, String key) {
        if (json == null) return null;
        String pattern = String.format("\\\"%s\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"", key);
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
        return m.find() ? m.group(1) : null;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    
    // Sends a generic error response
    private static void responseSafeError(HttpResponse response, String logMessage) throws IOException {
        System.err.println(logMessage);
        try {
            response.sendJson(500, "Internal Server Error", "{\"error\":\"Server error\"}");
        } catch (Exception ignored) {}
    }
} 
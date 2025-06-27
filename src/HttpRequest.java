import java.io.*;
import java.util.*;

/**
 * Represents an HTTP request. Parses the request line, method, path, and headers.
 */
public class HttpRequest {
    public String method;
    public String path;
    public String version;
    public Map<String, String> headers = new HashMap<>();
    public Map<String, String> params = new HashMap<>();
    public int contentLength = 0;
    public String body = null;

    /**
     * Parses an HTTP request from the input stream.
     */
    public static HttpRequest parse(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        HttpRequest req = new HttpRequest();

        // Parse request line: e.g., GET /index.html HTTP/1.1
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Empty request line");
        }
        String[] parts = requestLine.split(" ");
        req.method = parts[0];
        req.path = parts[1];
        req.version = parts.length > 2 ? parts[2] : "HTTP/1.1";

        // Parse headers
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int idx = line.indexOf(":");
            if (idx > 0) {
                String key = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();
                req.headers.put(key, value);
                if (key.equalsIgnoreCase("Content-Length")) {
                    try { req.contentLength = Integer.parseInt(value); } catch (NumberFormatException ignored) {}
                }
            }
        }
        // (Body parsing for POST can be added later)
        // Parse body if Content-Length is present
        if (req.contentLength > 0 && ("POST".equalsIgnoreCase(req.method) || "PUT".equalsIgnoreCase(req.method))) {
            char[] bodyChars = new char[req.contentLength];
            int read = reader.read(bodyChars);
            req.body = new String(bodyChars, 0, read);
        }
        return req;
    }
} 
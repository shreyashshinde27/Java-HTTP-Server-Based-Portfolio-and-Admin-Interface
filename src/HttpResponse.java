import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Represents and sends an HTTP response, including headers and body.
 */
public class HttpResponse {
    private final OutputStream output;
    private final HttpRequest request;
    private static final String SERVER_NAME = "CustomJavaHTTP/1.0";

    public HttpResponse(OutputStream output, HttpRequest request) {
        this.output = output;
        this.request = request;
    }

    /**
     * Sends the HTTP response based on the request.
     */
    public void send() throws IOException {
        File file = getFileForPath(request.path);
        if (file != null && file.exists() && file.isFile()) {
            sendFile(file, 200, "OK");
        } else {
            sendNotFound();
        }
    }

    /**
     * Maps the request path to a file in the web root.
     */
    private File getFileForPath(String path) {
        // Serve index.html for directory paths (e.g., /admin or /admin/)
        String relPath = path;
        if (relPath.equals("/")) {
            relPath = "/index.html";
        } else if (!relPath.contains(".") && !relPath.endsWith("/")) {
            // If path does not contain a dot (no file extension) and does not end with a slash, treat as directory
            relPath = relPath + "/index.html";
        } else if (relPath.endsWith("/")) {
            relPath = relPath + "index.html";
        }
        return new File(HttpServer.WEB_ROOT, relPath);
    }

    /**
     * Sends a 200 OK response with the file content.
     */
    private void sendFile(File file, int statusCode, String statusText) throws IOException {
        String contentType = getContentType(file.getName());
        byte[] content = readFileBytes(file);
        PrintWriter writer = new PrintWriter(output);
        writer.printf("HTTP/1.1 %d %s\r\n", statusCode, statusText);
        writer.printf("Date: %s\r\n", getServerTime());
        writer.printf("Server: %s\r\n", SERVER_NAME);
        writer.printf("Content-Type: %s\r\n", contentType);
        writer.printf("Content-Length: %d\r\n", content.length);
        writer.printf("Connection: close\r\n");
        writer.print("\r\n");
        writer.flush();
        output.write(content);
        output.flush();
    }

    /**
     * Sends a 404 Not Found response.
     */
    private void sendNotFound() throws IOException {
        String body = "<html><body><h1>404 Not Found</h1></body></html>";
        PrintWriter writer = new PrintWriter(output);
        writer.print("HTTP/1.1 404 Not Found\r\n");
        writer.printf("Date: %s\r\n", getServerTime());
        writer.printf("Server: %s\r\n", SERVER_NAME);
        writer.print("Content-Type: text/html\r\n");
        writer.printf("Content-Length: %d\r\n", body.length());
        writer.print("Connection: close\r\n");
        writer.print("\r\n");
        writer.print(body);
        writer.flush();
    }

    /**
     * Determines the Content-Type based on file extension.
     */
    private String getContentType(String filename) {
        if (filename.endsWith(".html") || filename.endsWith(".htm")) return "text/html";
        if (filename.endsWith(".css")) return "text/css";
        if (filename.endsWith(".js")) return "application/javascript";
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }

    /**
     * Reads the file content as bytes.
     */
    private byte[] readFileBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return fis.readAllBytes();
        }
    }

    /**
     * Returns the current server time in HTTP date format.
     */
    private String getServerTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date());
    }

    public void sendJson(int statusCode, String statusText, String jsonBody) throws IOException {
        PrintWriter writer = new PrintWriter(output);
        writer.printf("HTTP/1.1 %d %s\r\n", statusCode, statusText);
        writer.printf("Date: %s\r\n", getServerTime());
        writer.printf("Server: %s\r\n", SERVER_NAME);
        writer.print("Content-Type: application/json\r\n");
        writer.printf("Content-Length: %d\r\n", jsonBody.getBytes().length);
        writer.print("Connection: close\r\n");
        writer.print("\r\n");
        writer.print(jsonBody);
        writer.flush();
    }
} 
import java.io.*;
import java.net.Socket;

/**
 * Handles a single HTTP client connection. Parses the request and sends the response.
 */
public class RequestHandler implements Runnable {
    private final Socket clientSocket;
    private final Router router;

    public RequestHandler(Socket clientSocket, Router router) {
        this.clientSocket = clientSocket;
        this.router = router;
    }

    @Override
    public void run() {
        try (
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream()
        ) {
            // Parse the HTTP request
            HttpRequest request = HttpRequest.parse(input);
            // Prepare the HTTP response
            HttpResponse response = new HttpResponse(output, request);
            // Check for dynamic route
            RouteHandler handler = router.match(request);
            if (handler != null) {
                handler.handle(request, response);
            } else {
                response.send(); // fallback to static file serving
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
        }
    }
} 
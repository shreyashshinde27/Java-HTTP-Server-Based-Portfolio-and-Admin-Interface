import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The main HTTP server class. Sets up the TCP server socket, listens for incoming connections,
 * and spawns a new thread for each client connection.
 */
public class HttpServer {
    // The port the server will listen on
    public static final int PORT = 8080;
    // The root directory from which static files will be served
    public static final String WEB_ROOT = "web";

    public static void main(String[] args) {
        // Create and configure the router
        Router router = new Router();
        
        // --- Contact Message Routes ---
        // Public route for submitting a message
        router.post("/api/contact", MessageHandlers::handleCreateMessage);

        // Admin routes for managing messages
        router.get("/api/admin/messages", MessageHandlers::handleGetAllMessages);
        router.get("/api/admin/messages/{id}", MessageHandlers::handleGetMessage);
        router.put("/api/admin/messages/{id}", MessageHandlers::handleUpdateMessage);
        router.delete("/api/admin/messages/{id}", MessageHandlers::handleDeleteMessage);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("HTTP Server started on port " + PORT);
            while (true) {
                // Accept an incoming client connection (blocks until a connection is made)
                Socket clientSocket = serverSocket.accept();
                // Handle the connection in a new thread for concurrency
                new Thread(new RequestHandler(clientSocket, router)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
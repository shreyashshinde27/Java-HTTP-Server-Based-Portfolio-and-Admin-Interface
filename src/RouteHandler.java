import java.io.IOException;

@FunctionalInterface
public interface RouteHandler {
    void handle(HttpRequest request, HttpResponse response) throws IOException;
} 
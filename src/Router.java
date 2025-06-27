import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

/**
 * Router for registering and matching HTTP routes to handlers.
 */
public class Router {
    private final Map<String, Map<Pattern, RouteHandler>> routes = new HashMap<>();
    private final Map<String, Map<Pattern, List<String>>> routeParamNames = new HashMap<>();

    public void get(String path, RouteHandler handler) {
        addRoute("GET", path, handler);
    }

    public void post(String path, RouteHandler handler) {
        addRoute("POST", path, handler);
    }

    public void put(String path, RouteHandler handler) {
        addRoute("PUT", path, handler);
    }

    public void delete(String path, RouteHandler handler) {
        addRoute("DELETE", path, handler);
    }

    private void addRoute(String method, String path, RouteHandler handler) {
        method = method.toUpperCase();
        List<String> paramNames = new ArrayList<>();
        Pattern pattern = compilePath(path, paramNames);
        
        routes.computeIfAbsent(method, k -> new HashMap<>()).put(pattern, handler);
        routeParamNames.computeIfAbsent(method, k -> new HashMap<>()).put(pattern, paramNames);
    }

    private Pattern compilePath(String path, List<String> paramNames) {
        StringBuilder regex = new StringBuilder("^");
        Matcher matcher = Pattern.compile("\\{([^}]+)\\}").matcher(path);
        int lastIndex = 0;
        while (matcher.find()) {
            regex.append(Pattern.quote(path.substring(lastIndex, matcher.start())));
            paramNames.add(matcher.group(1));
            regex.append("([^/]+)");
            lastIndex = matcher.end();
        }
        regex.append(Pattern.quote(path.substring(lastIndex)));
        regex.append("$");
        return Pattern.compile(regex.toString());
    }

    public RouteHandler match(HttpRequest request) {
        String method = request.method.toUpperCase();
        String path = request.path;

        Map<Pattern, RouteHandler> methodRoutes = routes.get(method);
        if (methodRoutes != null) {
            for (Map.Entry<Pattern, RouteHandler> entry : methodRoutes.entrySet()) {
                Pattern pattern = entry.getKey();
                Matcher matcher = pattern.matcher(path);
                if (matcher.matches()) {
                    List<String> paramNames = routeParamNames.get(method).get(pattern);
                    for (int i = 0; i < matcher.groupCount(); i++) {
                        request.params.put(paramNames.get(i), matcher.group(i + 1));
                    }
                    return entry.getValue();
                }
            }
        }
        return null;
    }
} 
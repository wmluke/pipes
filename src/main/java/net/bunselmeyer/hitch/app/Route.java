package net.bunselmeyer.hitch.app;

public class Route {
    private final String method;
    private final String uriPattern;
    private final Middleware middleware;

    public Route(String method, String uriPattern, Middleware middleware) {
        this.method = method;
        this.uriPattern = uriPattern;
        this.middleware = middleware;
    }

    public String method() {
        return method;
    }

    public Middleware middleware() {
        return middleware;
    }

    public String uriPattern() {
        return uriPattern;
    }
}

package net.bunselmeyer.hitch.app;

import org.glassfish.jersey.uri.PathTemplate;

public class Route {
    private final String method;
    private final PathTemplate uriPattern;
    private final Middleware middleware;

    public Route(String method, String uriPattern, Middleware middleware) {
        this.method = method;
        this.uriPattern = new PathTemplate(uriPattern);
        this.middleware = middleware;
    }

    public String method() {
        return method;
    }

    public Middleware middleware() {
        return middleware;
    }

    public PathTemplate uriPattern() {
        return uriPattern;
    }
}

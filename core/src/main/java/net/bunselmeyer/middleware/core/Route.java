package net.bunselmeyer.middleware.core;

import net.bunselmeyer.middleware.core.middleware.Middleware;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.uri.PathTemplate;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Map;

public class Route {
    private final String method;
    private final PathTemplate uriPattern;
    private final PathMatcher pathMatcher;
    private final Middleware middleware;

    public Route(String method, String uriPattern, Middleware middleware) {
        this.method = method;
        this.uriPattern = uriPattern != null ? new PathTemplate(uriPattern) : null;
        this.pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + uriPattern);
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

    public PathMatcher pathMatcher() {
        return pathMatcher;
    }

    public boolean matches(String method, String requestUri, Map<String, String> pathParams, String contextPath) {
        if (StringUtils.stripToNull(this.method) != null && !StringUtils.equalsIgnoreCase(this.method, method)) {
            return false;
        }
        Path path = Paths.get(contextPath, requestUri);
        boolean matchesPath = pathMatcher.matches(path);
        boolean matchesPathParams = uriPattern.match(path.toString(), pathParams);
        return matchesPath || matchesPathParams;
    }
}

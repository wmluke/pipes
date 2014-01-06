package net.bunselmeyer.hitch.app;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.uri.PathTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AppImpl implements App {

    private final List<Route> routes = new ArrayList<>();

    @Override
    public App use(MiddlewareFactory middlewareFactory) {
        return use(middlewareFactory.build());
    }

    @Override
    public App use(Middleware middleware) {
        routes.add(new Route(null, "*", middleware));
        return this;
    }

    @Override
    public Stream<Route> routes(Request req) {
        String method = req.method();
        return routes.stream().filter((r) -> {
            // match method
            if (StringUtils.stripToNull(r.method()) != null && !StringUtils.equalsIgnoreCase(r.method(), method)) {
                return false;
            }
            // thank you jersey-common for the uri pattern matching!
            // todo: prob should move PathTemplate into Route, so that its not created with every request
            PathTemplate pathTemplate = new PathTemplate(r.uriPattern());
            return pathTemplate.match(req.uri(), req.routeParams());
        });
    }

    @Override
    public App get(String uriPattern, Middleware middleware) {
        routes.add(new Route("GET", uriPattern, middleware));
        return this;
    }

    @Override
    public App post(String uriPattern, Middleware middleware) {
        routes.add(new Route("POST", uriPattern, middleware));
        return this;
    }

    @Override
    public App put(String uriPattern, Middleware middleware) {
        routes.add(new Route("PUT", uriPattern, middleware));
        return this;
    }

    @Override
    public App delete(String uriPattern, Middleware middleware) {
        routes.add(new Route("DELETE", uriPattern, middleware));
        return this;
    }

}

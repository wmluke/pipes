package net.bunselmeyer.hitch.app;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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

    @Override
    public Stream<Route> routes(Request req) {
        String method = req.method();
        return routes.stream().filter((r) -> {
            // match method
            if (StringUtils.stripToNull(r.method()) != null && !StringUtils.equalsIgnoreCase(r.method(), method)) {
                return false;
            }
            // thank you jersey-common for the uri pattern matching!
            return r.uriPattern().match(req.uri(), req.routeParams());
        });
    }

    @Override
    public void dispatch(Request req, Response res) throws IOException {
        Iterator<Route> iterator = routes(req).iterator();
        if (!iterator.hasNext()) {
            res.send(404, "404 Not found");
        }
        while (iterator.hasNext()) {
            Route route = iterator.next();
            if (!res.isCommitted()) {
                route.middleware().run(req, res);
            }
        }
    }

}

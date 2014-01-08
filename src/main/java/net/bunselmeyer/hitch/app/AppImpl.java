package net.bunselmeyer.hitch.app;

import net.bunselmeyer.hitch.http.HttpRequest;
import net.bunselmeyer.hitch.http.HttpResponse;
import net.bunselmeyer.hitch.middleware.Middleware;
import net.bunselmeyer.hitch.middleware.MiddlewareFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class AppImpl implements App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private final Configuration configuration = new AppConfiguration();
    private final List<Route> routes = new ArrayList<>();

    @Override
    public App configure(Consumer<Configuration> consumer) {
        consumer.accept(configuration);
        return this;
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    @Override
    public App use(MiddlewareFactory middlewareFactory) {
        return use(middlewareFactory.build());
    }

    @Override
    public App use(Middleware.BasicMiddleware middleware) {
        routes.add(new Route(null, null, middleware));
        return this;
    }

    @Override
    public App use(Middleware.IntermediateMiddleware middleware) {
        routes.add(new Route(null, null, middleware));
        return this;
    }

    @Override
    public App use(Middleware.AdvancedMiddleware middleware) {
        routes.add(new Route(null, null, middleware));
        return this;
    }

    @Override
    public App get(String uriPattern, Middleware.BasicMiddleware middleware) {
        routes.add(new Route("GET", uriPattern, middleware));
        return this;
    }

    @Override
    public App post(String uriPattern, Middleware.BasicMiddleware middleware) {
        routes.add(new Route("POST", uriPattern, middleware));
        return this;
    }

    @Override
    public App put(String uriPattern, Middleware.BasicMiddleware middleware) {
        routes.add(new Route("PUT", uriPattern, middleware));
        return this;
    }

    @Override
    public App delete(String uriPattern, Middleware.BasicMiddleware middleware) {
        routes.add(new Route("DELETE", uriPattern, middleware));
        return this;
    }

    @Override
    public Stream<Route> routes(HttpRequest req) {
        String method = req.method();
        return routes.stream().filter((r) -> {
            // match method
            if (StringUtils.stripToNull(r.method()) != null && !StringUtils.equalsIgnoreCase(r.method(), method)) {
                return false;
            }
            // thank you jersey-common for the uri pattern matching!
            return r.uriPattern() == null || r.uriPattern().match(req.uri(), req.routeParams());
        });
    }

    @Override
    public void dispatch(HttpRequest req, HttpResponse res) throws IOException {
        final Iterator<Route> stack = routes(req).iterator();
        Next next = new Next(stack, req, res);
        next.run(null);

    }

    private static class Next implements Middleware.Next {

        private final Iterator<Route> stack;
        private final HttpRequest req;
        private final HttpResponse res;

        private Next(Iterator<Route> stack, HttpRequest req, HttpResponse res) {
            this.stack = stack;
            this.req = req;
            this.res = res;
        }

        @Override
        public void run(Exception err) {
            // unhandled request
            if (!stack.hasNext()) {
                if (err != null) {
                    // unhandled error
                    if (res.status() < 400) {
                        res.status(500);
                    }
                    res.type("text/html");
                    res.charset("UTF-8");
                    res.send("Internal server error");
                    logger.error(err.getMessage());
                    throw new RuntimeException(err);

                } else {
                    res.send(404, "404 Not found");
                }
            }

            if (res.isCommitted()) {
                return;
            }

            Route route = stack.next();
            runner(route.middleware(), err, req, res, this);
        }

        private void runner(Middleware middleware, Exception err, HttpRequest req, HttpResponse res, Middleware.Next next) {
            try {
                if (err != null) {
                    if (middleware instanceof Middleware.AdvancedMiddleware) {
                        ((Middleware.AdvancedMiddleware) middleware).run(err, req, res, next);
                    } else {
                        next.run(err);
                    }

                } else if (middleware instanceof Middleware.IntermediateMiddleware) {
                    ((Middleware.IntermediateMiddleware) middleware).run(req, res, next);

                } else if (middleware instanceof Middleware.BasicMiddleware) {
                    ((Middleware.BasicMiddleware) middleware).run(req, res);
                    next.run(null);

                } else {
                    next.run(null);
                }
            } catch (Exception e) {
                next.run(e);
            }
        }
    }

}

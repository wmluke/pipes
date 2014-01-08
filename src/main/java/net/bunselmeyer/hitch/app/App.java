package net.bunselmeyer.hitch.app;

import net.bunselmeyer.hitch.http.HttpRequest;
import net.bunselmeyer.hitch.http.HttpResponse;
import net.bunselmeyer.hitch.middleware.Middleware;
import net.bunselmeyer.hitch.middleware.MiddlewareFactory;

import java.io.IOException;
import java.util.stream.Stream;

public interface App {

    App use(MiddlewareFactory middlewareFactory);

    App use(Middleware.BasicMiddleware middleware);

    App use(Middleware.AdvancedMiddleware middleware);

    App use(Middleware.IntermediateMiddleware middleware);

    App get(String uriPattern, Middleware.BasicMiddleware middleware);

    App post(String uriPattern, Middleware.BasicMiddleware middleware);

    App put(String uriPattern, Middleware.BasicMiddleware middleware);

    App delete(String uriPattern, Middleware.BasicMiddleware middleware);

    Stream<Route> routes(HttpRequest request);

    void dispatch(HttpRequest req, HttpResponse res) throws IOException;

    static App create() {
        return new AppImpl();
    }
}

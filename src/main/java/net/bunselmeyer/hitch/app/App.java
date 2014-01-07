package net.bunselmeyer.hitch.app;

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

    Stream<Route> routes(Request request);

    void dispatch(Request req, Response res) throws IOException;

    static App create() {
        return new AppImpl();
    }
}

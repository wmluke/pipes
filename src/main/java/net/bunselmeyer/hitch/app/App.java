package net.bunselmeyer.hitch.app;

import java.util.stream.Stream;

public interface App {

    App use(MiddlewareFactory middlewareFactory);

    App use(Middleware middleware);

    App get(String uriPattern, Middleware middleware);

    App post(String uriPattern, Middleware middleware);

    App put(String uriPattern, Middleware middleware);

    App delete(String uriPattern, Middleware middleware);

    Stream<Route> routes(Request request);

    static App create() {
        return new AppImpl();
    }
}

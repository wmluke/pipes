package net.bunselmeyer.hitch.app;

import java.util.List;

public interface App {

    App use(MiddlewareFactory middlewareFactory);

    App use(Middleware middleware);

    App get(String uriPattern, Middleware middleware);

    App post(String uriPattern, Middleware middleware);

    App put(String uriPattern, Middleware middleware);

    App delete(String uriPattern, Middleware middleware);

    List<Middleware> middleware();

    static App create() {
        return new AppImpl();
    }
}

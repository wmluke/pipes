package net.bunselmeyer.hitch.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppImpl implements App {

    private final List<Middleware> middlewares = new ArrayList<>();

    @Override
    public App use(MiddlewareFactory middlewareFactory) {
        middlewares.add(middlewareFactory.build());
        return this;
    }

    @Override
    public App use(Middleware middleware) {
        middlewares.add(middleware);
        return this;
    }

    @Override
    public List<Middleware> middleware() {
        return Collections.unmodifiableList(middlewares);
    }

    @Override
    public App get(String uriPattern, Middleware middleware) {
        // TODO: implement this
        return this;
    }

    @Override
    public App post(String uriPattern, Middleware middleware) {
        // TODO: implement this
        return this;
    }

    @Override
    public App put(String uriPattern, Middleware middleware) {
        // TODO: implement this
        return this;
    }

    @Override
    public App delete(String uriPattern, Middleware middleware) {
        // TODO: implement this
        return this;
    }
}

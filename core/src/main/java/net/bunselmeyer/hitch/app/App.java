package net.bunselmeyer.hitch.app;

import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bunselmeyer.hitch.middleware.Middleware;
import net.bunselmeyer.hitch.middleware.MiddlewareFactory;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface App<Q, P> {

    <T extends Middleware> App<Q, P> use(MiddlewareFactory<T> middlewareFactory);

    App<Q, P> use(App<Q, P> app);

    App<Q, P> use(Middleware.BasicMiddleware<Q, P> middleware);

    App<Q, P> use(Middleware.AdvancedMiddleware<Q, P> middleware);

    App<Q, P> use(Middleware.IntermediateMiddleware<Q, P> middleware);

    App<Q, P> get(String uriPattern, Middleware.BasicMiddleware<Q, P> middleware);

    App<Q, P> post(String uriPattern, Middleware.BasicMiddleware<Q, P> middleware);

    App<Q, P> put(String uriPattern, Middleware.BasicMiddleware<Q, P> middleware);

    App<Q, P> delete(String uriPattern, Middleware.BasicMiddleware<Q, P> middleware);

    App<Q, P> configure(Consumer<Configuration> consumer);

    Stream<Route> routes(Q request);

    Stream<Route> routes(Q req, String contextPath);

    void dispatch(Q req, P res) throws IOException;

    void dispatch(Q req, P res, String contextPath) throws IOException;

    Configuration configuration();

    public static interface Configuration {

        ObjectMapper jsonObjectMapper();

        ObjectMapper xmlObjectMapper();

        LoggerContext loggerContext();
    }
}

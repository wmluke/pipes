package net.bunselmeyer.hitch;

import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bunselmeyer.hitch.middleware.Middleware;

import java.io.IOException;
import java.util.function.Consumer;

public interface App<Q, P> {

    App<Q, P> configure(Consumer<Configuration> consumer);

    Configuration configuration();

//    <T extends Middleware> HitchApp<Q, P> use(MiddlewareFactory<T> middlewareFactory);

    App<Q, P> use(App<Q, P> app);

    App<Q, P> use(Middleware.BasicMiddleware<Q, P> middleware);

    App<Q, P> use(Middleware.AdvancedMiddleware<Q, P> middleware);

    App<Q, P> use(Middleware.IntermediateMiddleware<Q, P> middleware);

    void dispatch(Q req, P res) throws IOException;

    public static interface Configuration {

        ObjectMapper jsonObjectMapper();

        ObjectMapper xmlObjectMapper();

        LoggerContext loggerContext();
    }

    @SuppressWarnings("unchecked")
    public static <Q, P> void runner(Middleware middleware, Exception err, Q req, P res, Middleware.Next next) {
        try {
            if (err != null) {
                if (middleware instanceof Middleware.AdvancedMiddleware) {
                    ((Middleware.AdvancedMiddleware<Q, P>) middleware).run(err, req, res, next);
                } else {
                    next.run(err);
                }

            } else if (middleware instanceof Middleware.IntermediateMiddleware) {
                ((Middleware.IntermediateMiddleware<Q, P>) middleware).run(req, res, next);

            } else if (middleware instanceof Middleware.BasicMiddleware) {
                ((Middleware.BasicMiddleware<Q, P>) middleware).run(req, res);
                next.run(null);

            } else {
                next.run(null);
            }
        } catch (Exception e) {
            next.run(e);
        }
    }
}

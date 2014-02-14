package net.bunselmeyer.hitch;

import net.bunselmeyer.hitch.middleware.Middleware;

import java.io.IOException;
import java.util.function.Consumer;

public interface App<Q, P> {

    <C> App<Q, P> configure(Class<C> type, Consumer<C> consumer) throws IllegalAccessException, InstantiationException;

    <C> App<Q, P> configure(Class<C> type, String name, Consumer<C> consumer) throws IllegalAccessException, InstantiationException;

    <C> App<Q, P> configure(C configuration, Consumer<C> consumer);

    <C> App<Q, P> configure(C configuration, String name, Consumer<C> consumer);

    <C> C configuration(Class<C> type);

    <C> C configuration(Class<C> type, String name);

    App<Q, P> use(App<Q, P> app);

    App<Q, P> use(Middleware.BasicMiddleware<Q, P> middleware);

    App<Q, P> use(Middleware.AdvancedMiddleware<Q, P> middleware);

    App<Q, P> use(Middleware.IntermediateMiddleware<Q, P> middleware);

    void dispatch(Q req, P res) throws IOException;

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

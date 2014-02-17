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

    App<Q, P> use(Middleware.IntermediateMiddleware<Q, P> middleware);

    App<Q, P> use(Middleware.ExceptionMiddleware<Q, P> middleware);

    <E extends Throwable> App<Q, P> use(Class<E> exceptionType, Middleware.CheckedExceptionMiddleware<Q, P, E> middleware);

    void dispatch(Q req, P res) throws IOException;

    public static interface MiddlewarePipeline<Q, P> {

        MiddlewarePipeline<Q, P> pipe(Middleware.BasicMiddleware<Q, P> middleware);

        MiddlewarePipeline<Q, P> pipe(Middleware.IntermediateMiddleware<Q, P> middleware);

        MiddlewarePipeline<Q, P> pipe(Middleware.ExceptionMiddleware<Q, P> middleware);

    }


    @SuppressWarnings("unchecked")
    public static <Q, P> void runner(Middleware middleware, Exception err, Q req, P res, Middleware.Next next) {
        try {
            if (err != null) {
                if (middleware instanceof Middleware.ExceptionMiddleware) {
                    ((Middleware.ExceptionMiddleware<Q, P>) middleware).run(err, req, res, next);
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

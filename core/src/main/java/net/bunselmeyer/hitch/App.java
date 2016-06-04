package net.bunselmeyer.hitch;

import net.bunselmeyer.hitch.middleware.Middleware;
import net.bunselmeyer.hitch.middleware.Next;

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

    App<Q, P> use(Middleware.StandardMiddleware1<Q, P> middleware);

    <M> App<Q, P> use(Middleware.StandardMiddleware2<Q, P, M> middleware);

    <M, N> App<Q, P> use(Middleware.StandardMiddleware3<Q, P, M, N> middleware);

    App<Q, P> use(Middleware.StandardMiddleware4<Q, P> middleware);

    <M> App<Q, P> use(Middleware.StandardMiddleware5<Q, P, M> middleware);

    App<Q, P> onError(Middleware.ExceptionMiddleware<Q, P> middleware);

    <E extends Throwable> App<Q, P> onError(Class<E> exceptionType, Middleware.CheckedExceptionMiddleware<Q, P, E> middleware);

    void dispatch(Q req, P res, Next next) throws IOException;

    interface MiddlewarePipeline<Q, P> {

        MiddlewarePipeline<Q, P> pipe(Middleware.StandardMiddleware1<Q, P> middleware);

        <M> MemoMiddlewarePipeline<Q, P, M> pipe(Middleware.StandardMiddleware2<Q, P, M> middleware);

        MiddlewarePipeline<Q, P> pipe(Middleware.StandardMiddleware4<Q, P> middleware);

        MiddlewarePipeline<Q, P> pipe(Middleware.ExceptionMiddleware<Q, P> middleware);

    }

    interface MemoMiddlewarePipeline<Q, P, M> {

        <N> MemoMiddlewarePipeline<Q, P, N> pipe(Middleware.StandardMiddleware3<Q, P, M, N> middleware);

        MiddlewarePipeline<Q, P> pipe(Middleware.StandardMiddleware4<Q, P> middleware);

        MemoMiddlewarePipeline<Q, P, M> pipe(Middleware.StandardMiddleware5<Q, P, M> middleware);
    }


    @SuppressWarnings("unchecked")
    static <Q, P> void runner(Middleware middleware, Exception err, Q req, P res, Next next) {
        try {
            if (err != null) {
                if (middleware instanceof Middleware.ExceptionMiddleware) {
                    ((Middleware.ExceptionMiddleware<Q, P>) middleware).run(err, req, res, next);
                } else {
                    next.run(err);
                }

            } else if (middleware instanceof Middleware.StandardMiddleware4) {
                ((Middleware.StandardMiddleware4<Q, P>) middleware).run(req, res, next);

            } else if (middleware instanceof Middleware.StandardMiddleware1) {
                ((Middleware.StandardMiddleware1<Q, P>) middleware).run(req, res);
                next.run(null);

            } else {
                next.run(null);
            }
        } catch (Exception e) {
            next.run(e);
        }
    }
}

package net.bunselmeyer.middleware.core;

import net.bunselmeyer.middleware.core.middleware.Middleware;

import java.io.IOException;
import java.util.function.Consumer;

public interface App<Q, P, A extends App> {

    <C> A configure(Class<C> type, Consumer<C> consumer) throws IllegalAccessException, InstantiationException;

    <C> A configure(Class<C> type, String name, Consumer<C> consumer) throws IllegalAccessException, InstantiationException;

    <C> A configure(C configuration, Consumer<C> consumer);

    <C> A configure(C configuration, String name, Consumer<C> consumer);

    <C> C configuration(Class<C> type);

    <C> C configuration(Class<C> type, String name);

    A use(App<Q, P, ?> app);

    A use(Middleware.StandardMiddleware1<Q, P> middleware);

    <M> A use(Middleware.StandardMiddleware2<Q, P, M> middleware);

    <M, N> A use(Middleware.StandardMiddleware3<Q, P, M, N> middleware);

    A use(Middleware.StandardMiddleware4<Q, P> middleware);

    <M> A use(Middleware.StandardMiddleware5<Q, P, M> middleware);

    A onError(Middleware.ExceptionMiddleware<Q, P> middleware);

    <E extends Throwable> A onError(Class<E> exceptionType, Middleware.CheckedExceptionMiddleware<Q, P, E> middleware);

    void dispatch(Q req, P res, Next next) throws IOException;

}

package net.bunselmeyer.middleware.core;

import net.bunselmeyer.middleware.core.middleware.Middleware;

public interface App<Q, P, A extends App> extends ConfigurableApp<A>, RunnableApp<Q, P> {

    A use(Middleware.StandardMiddleware1<Q, P> middleware);

    <M> A use(Middleware.StandardMiddleware2<Q, P, M> middleware);

    <M, N> A use(Middleware.StandardMiddleware3<Q, P, M, N> middleware);

    A use(Middleware.StandardMiddleware4<Q, P> middleware);

    <M> A use(Middleware.StandardMiddleware5<Q, P, M> middleware);

    A onError(Middleware.ExceptionMiddleware<Q, P> middleware);

    <E extends Throwable> A onError(Class<E> exceptionType, Middleware.CheckedExceptionMiddleware<Q, P, E> middleware);


}

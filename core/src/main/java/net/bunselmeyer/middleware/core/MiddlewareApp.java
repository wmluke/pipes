package net.bunselmeyer.middleware.core;


import net.bunselmeyer.middleware.core.middleware.Middleware;

public interface MiddlewareApp<Q, P, A extends MiddlewareApp> {

    <C> C configuration(Class<C> type);

    <C> C configuration(Class<C> type, String name);

    A use(Middleware.StandardMiddleware1<Q, P> middleware);

    <M> A use(Middleware.StandardMiddleware2<Q, P, M> middleware);

    <M, N> A use(Middleware.StandardMiddleware3<Q, P, M, N> middleware);

    A use(Middleware.StandardMiddleware4<Q, P> middleware);

    <M> A use(Middleware.StandardMiddleware5<Q, P, M> middleware);
}

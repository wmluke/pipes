package net.bunselmeyer.middleware.core;


import net.bunselmeyer.middleware.core.middleware.Middleware;

public interface RunnableApp<Q, P> extends Middleware.StandardMiddleware4<Q, P> {

    <C> C configuration(Class<C> type);

    <C> C configuration(Class<C> type, String name);

}

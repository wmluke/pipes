package net.bunselmeyer.middleware.core;

import net.bunselmeyer.middleware.core.middleware.Middleware;

public interface App<Q, P, A extends App> extends ConfigurableApp<A>, MiddlewareApp<Q, P, A>, RunnableApp<Q, P> {

    A onError(Middleware.ExceptionMiddleware<Q, P> middleware);

    <E extends Throwable> A onError(Class<E> exceptionType, Middleware.CheckedExceptionMiddleware<Q, P, E> middleware);


}

package net.bunselmeyer.hitch.middleware;

import java.util.function.Supplier;

public interface MiddlewareFactory<T extends Middleware> extends Supplier<T> {


    interface Basic extends MiddlewareFactory<Middleware.StandardMiddleware1> {

    }

    interface Intermediate extends MiddlewareFactory<Middleware.StandardMiddleware4> {

    }

    public interface Advanced extends MiddlewareFactory<Middleware.ExceptionMiddleware> {

    }

}

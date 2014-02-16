package net.bunselmeyer.hitch.middleware;

import java.util.function.Supplier;

public interface MiddlewareFactory<T extends Middleware> extends Supplier<T> {


    public interface Basic extends MiddlewareFactory<Middleware.BasicMiddleware> {

    }

    public interface Intermediate extends MiddlewareFactory<Middleware.IntermediateMiddleware> {

    }

    public interface Advanced extends MiddlewareFactory<Middleware.ExceptionMiddleware> {

    }

}

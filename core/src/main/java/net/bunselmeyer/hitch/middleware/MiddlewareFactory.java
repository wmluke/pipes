package net.bunselmeyer.hitch.middleware;

public interface MiddlewareFactory {

    Middleware.BasicMiddleware build();
}

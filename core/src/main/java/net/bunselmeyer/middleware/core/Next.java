package net.bunselmeyer.middleware.core;

import net.bunselmeyer.middleware.core.middleware.Middleware;

public interface Next {

    <T> T memo();

    void run(Object memo);

    void visit(Middleware middleware, Object memo);

    void visit(Middleware.StandardMiddleware1 middleware, Object memo);

    void visit(Middleware.StandardMiddleware2 middleware, Object memo);

    void visit(Middleware.StandardMiddleware3 middleware, Object memo);

    void visit(Middleware.StandardMiddleware4 middleware, Object memo);

    void visit(Middleware.StandardMiddleware5 middleware, Object memo);
    
    void visit(Middleware.ExceptionMiddleware middleware, Object memo);

    <Q, P, E extends Throwable> void visit(Middleware.CheckedExceptionMiddleware<Q, P, E> middleware, Object memo);

}

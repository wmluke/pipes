package net.bunselmeyer.middleware.core;

import net.bunselmeyer.middleware.core.middleware.Middleware;
import net.bunselmeyer.middleware.pipes.Pipes;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import net.bunselmeyer.middleware.pipes.http.servlet.ServletApp;

import java.io.IOException;

public interface PipesApp<Q, P, A extends App> extends App<Q, P, A> {

    MiddlewarePipeline<Q, P> get(String uriPattern);

    MiddlewarePipeline<Q, P> post(String uriPattern);

    MiddlewarePipeline<Q, P> put(String uriPattern);

    MiddlewarePipeline<Q, P> delete(String uriPattern);

    void dispatch(Q req, P res, String contextPath) throws IOException;

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
}

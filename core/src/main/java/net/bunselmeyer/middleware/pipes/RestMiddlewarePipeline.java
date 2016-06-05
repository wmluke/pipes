package net.bunselmeyer.middleware.pipes;

import net.bunselmeyer.middleware.core.App;
import net.bunselmeyer.middleware.core.RoutableApp;
import net.bunselmeyer.middleware.core.middleware.Middleware;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import net.bunselmeyer.middleware.pipes.middleware.RouteMiddleware;

public class RestMiddlewarePipeline implements RoutableApp.MiddlewarePipeline<HttpRequest, HttpResponse>, RoutableApp.MemoMiddlewarePipeline<HttpRequest, HttpResponse, Object> {

    private final App<HttpRequest, HttpResponse, ?> app;
    private final String method;
    private final String uriPattern;

    public RestMiddlewarePipeline(App<HttpRequest, HttpResponse, ?> app, String method, String uriPattern) {
        this.app = app;
        this.method = method;
        this.uriPattern = uriPattern;
    }

    @Override
    public RoutableApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipe(Middleware.StandardMiddleware1<HttpRequest, HttpResponse> middleware) {
        app.use(RouteMiddleware.route(method.toUpperCase(), uriPattern, middleware));
        return this;
    }

    @Override
    public <M> RoutableApp.MemoMiddlewarePipeline<HttpRequest, HttpResponse, M> pipe(Middleware.StandardMiddleware2<HttpRequest, HttpResponse, M> middleware) {
        app.use(RouteMiddleware.route(method.toUpperCase(), uriPattern, middleware));
        return (RoutableApp.MemoMiddlewarePipeline<HttpRequest, HttpResponse, M>) this;
    }

    @Override
    public <N> RoutableApp.MemoMiddlewarePipeline<HttpRequest, HttpResponse, N> pipe(Middleware.StandardMiddleware3<HttpRequest, HttpResponse, Object, N> middleware) {
        app.use(RouteMiddleware.route(method.toUpperCase(), uriPattern, middleware));
        return (RoutableApp.MemoMiddlewarePipeline<HttpRequest, HttpResponse, N>) this;

    }

    @Override
    public RoutableApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipe(Middleware.StandardMiddleware4<HttpRequest, HttpResponse> middleware) {
        app.use(RouteMiddleware.route(method.toUpperCase(), uriPattern, middleware));
        return this;
    }

    @Override
    public RoutableApp.MemoMiddlewarePipeline<HttpRequest, HttpResponse, Object> pipe(Middleware.StandardMiddleware5<HttpRequest, HttpResponse, Object> middleware) {
        app.use(RouteMiddleware.route(method.toUpperCase(), uriPattern, middleware));
        return this;
    }

    @Override
    public RoutableApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipe(Middleware.ExceptionMiddleware<HttpRequest, HttpResponse> middleware) {
        app.onError(RouteMiddleware.route(method.toUpperCase(), uriPattern, middleware));
        return this;
    }
}

package net.bunselmeyer.evince;

import net.bunselmeyer.evince.http.HttpRequest;
import net.bunselmeyer.evince.http.HttpResponse;
import net.bunselmeyer.evince.middleware.RouteMiddleware;
import net.bunselmeyer.hitch.App;
import net.bunselmeyer.hitch.middleware.Middleware;

public class RestMiddlewarePipeline implements App.MiddlewarePipeline<HttpRequest, HttpResponse>, App.MemoMiddlewarePipeline<HttpRequest, HttpResponse, Object> {

    private final App<HttpRequest, HttpResponse> app;
    private final String method;
    private final String uriPattern;

    public RestMiddlewarePipeline(App<HttpRequest, HttpResponse> app, String method, String uriPattern) {
        this.app = app;
        this.method = method;
        this.uriPattern = uriPattern;
    }

    @Override
    public App.MiddlewarePipeline<HttpRequest, HttpResponse> pipe(Middleware.StandardMiddleware1<HttpRequest, HttpResponse> middleware) {
        app.use(RouteMiddleware.route(method.toUpperCase(), uriPattern, middleware));
        return this;
    }

    @Override
    public <M> App.MemoMiddlewarePipeline<HttpRequest, HttpResponse, M> pipe(Middleware.StandardMiddleware2<HttpRequest, HttpResponse, M> middleware) {
        app.use(RouteMiddleware.route(method.toUpperCase(), uriPattern, middleware));
        return (App.MemoMiddlewarePipeline<HttpRequest, HttpResponse, M>) this;
    }

    @Override
    public <N> App.MemoMiddlewarePipeline<HttpRequest, HttpResponse, N> pipe(Middleware.StandardMiddleware3<HttpRequest, HttpResponse, Object, N> middleware) {
        app.use(RouteMiddleware.route(method.toUpperCase(), uriPattern, middleware));
        return (App.MemoMiddlewarePipeline<HttpRequest, HttpResponse, N>) this;

    }

    @Override
    public App.MiddlewarePipeline<HttpRequest, HttpResponse> pipe(Middleware.StandardMiddleware4<HttpRequest, HttpResponse> middleware) {
        app.use(RouteMiddleware.route(method.toUpperCase(), uriPattern, middleware));
        return this;
    }

    @Override
    public App.MemoMiddlewarePipeline<HttpRequest, HttpResponse, Object> pipe(Middleware.StandardMiddleware5<HttpRequest, HttpResponse, Object> middleware) {
        app.use(RouteMiddleware.route(method.toUpperCase(), uriPattern, middleware));
        return this;
    }

    @Override
    public App.MiddlewarePipeline<HttpRequest, HttpResponse> pipe(Middleware.ExceptionMiddleware<HttpRequest, HttpResponse> middleware) {
        app.onError(RouteMiddleware.route(method.toUpperCase(), uriPattern, middleware));
        return this;
    }
}

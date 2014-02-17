package net.bunselmeyer.evince;

import net.bunselmeyer.evince.http.HttpRequest;
import net.bunselmeyer.evince.http.HttpResponse;
import net.bunselmeyer.evince.middleware.RouteMiddleware;
import net.bunselmeyer.hitch.App;
import net.bunselmeyer.hitch.middleware.Middleware;

public class RestMiddlewarePipeline implements App.MiddlewarePipeline<HttpRequest, HttpResponse> {

    private final App<HttpRequest, HttpResponse> app;
    private final String method;
    private final String uriPattern;

    public RestMiddlewarePipeline(App<HttpRequest, HttpResponse> app, String method, String uriPattern) {
        this.app = app;
        this.method = method;
        this.uriPattern = uriPattern;
    }

    @Override
    public App.MiddlewarePipeline<HttpRequest, HttpResponse> pipe(Middleware.BasicMiddleware<HttpRequest, HttpResponse> middleware) {
        app.use(RouteMiddleware.route(method.toUpperCase(), uriPattern, middleware));
        return this;
    }

    @Override
    public App.MiddlewarePipeline<HttpRequest, HttpResponse> pipe(Middleware.IntermediateMiddleware<HttpRequest, HttpResponse> middleware) {
        app.use(RouteMiddleware.route(method.toUpperCase(), uriPattern, middleware));
        return this;
    }

    @Override
    public App.MiddlewarePipeline<HttpRequest, HttpResponse> pipe(Middleware.ExceptionMiddleware<HttpRequest, HttpResponse> middleware) {
        app.use(RouteMiddleware.route(method.toUpperCase(), uriPattern, middleware));
        return this;
    }

}

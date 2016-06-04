package net.bunselmeyer.evince.middleware;

import net.bunselmeyer.evince.Route;
import net.bunselmeyer.evince.http.HttpRequest;
import net.bunselmeyer.evince.http.HttpResponse;
import net.bunselmeyer.hitch.middleware.Middleware;

public class RouteMiddleware {

    public static <M> Middleware.StandardMiddleware5<HttpRequest, HttpResponse, M> route(String method, String uriPattern, Middleware.StandardMiddleware1<HttpRequest, HttpResponse> middleware) {
        Route route = new Route(method, uriPattern, middleware);
        return (memo, request, response, next) -> {
            if (route.matches(request.method(), request.uri(), request.routeParams(), "")) {
                middleware.run(request, response);
            }
            next.run(memo);
        };
    }

    public static <M> Middleware.StandardMiddleware5<HttpRequest, HttpResponse, M> route(String method, String uriPattern, Middleware.StandardMiddleware2<HttpRequest, HttpResponse, M> middleware) {
        Route route = new Route(method, uriPattern, middleware);
        return (memo, request, response, next) -> {
            if (route.matches(request.method(), request.uri(), request.routeParams(), "")) {
                next.run(middleware.run(request, response));
                return;
            }
            next.run(memo);
        };
    }


    public static <M, N> Middleware.StandardMiddleware5<HttpRequest, HttpResponse, M> route(String method, String uriPattern, Middleware.StandardMiddleware3<HttpRequest, HttpResponse, M, N> middleware) {
        Route route = new Route(method, uriPattern, middleware);
        return (memo, request, response, next) -> {
            if (route.matches(request.method(), request.uri(), request.routeParams(), "")) {
                next.run(middleware.run(memo, request, response));
                return;
            }
            next.run(memo);
        };
    }

    public static <M> Middleware.StandardMiddleware5<HttpRequest, HttpResponse, M> route(String method, String uriPattern, Middleware.StandardMiddleware4<HttpRequest, HttpResponse> middleware) {
        Route route = new Route(method, uriPattern, middleware);
        return (memo, request, response, next) -> {
            if (route.matches(request.method(), request.uri(), request.routeParams(), "")) {
                middleware.run(request, response, next);
                return;
            }
            next.run(memo);
        };
    }

    public static <M> Middleware.StandardMiddleware5<HttpRequest, HttpResponse, M> route(String method, String uriPattern, Middleware.StandardMiddleware5<HttpRequest, HttpResponse, M> middleware) {
        Route route = new Route(method, uriPattern, middleware);
        return (memo, request, response, next) -> {
            if (route.matches(request.method(), request.uri(), request.routeParams(), "")) {
                middleware.run(memo, request, response, next);
                return;
            }
            next.run(memo);
        };
    }

    public static Middleware.ExceptionMiddleware<HttpRequest, HttpResponse> route(String method, String uriPattern, Middleware.ExceptionMiddleware<HttpRequest, HttpResponse> middleware) {
        Route route = new Route(method, uriPattern, middleware);
        return (e, request, response, next) -> {
            if (route.matches(request.method(), request.uri(), request.routeParams(), "")) {
                middleware.run(e, request, response, next);
                return;
            }
            next.run(e);
        };
    }

}

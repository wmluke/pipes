package net.bunselmeyer.evince.middleware;

import net.bunselmeyer.evince.Route;
import net.bunselmeyer.evince.http.HttpRequest;
import net.bunselmeyer.evince.http.HttpResponse;
import net.bunselmeyer.hitch.middleware.Middleware;

public class RouteMiddleware {

    public static Middleware.IntermediateMiddleware<HttpRequest, HttpResponse> route(String method, String uriPattern, Middleware.BasicMiddleware<HttpRequest, HttpResponse> middleware) {
        Route route = new Route(method, uriPattern, middleware);
        return (request, response, next) -> {
            if (route.matches(request.method(), request.uri(), request.routeParams(), "")) {
                middleware.run(request, response);
            }
            next.run(null);
        };
    }
}
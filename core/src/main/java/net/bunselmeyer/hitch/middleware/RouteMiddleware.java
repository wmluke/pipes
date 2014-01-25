package net.bunselmeyer.hitch.middleware;

import net.bunselmeyer.evince.Route;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static net.bunselmeyer.evince.http.servlet.HttpRequestServletAdapter.PATH_PARAMS;

public class RouteMiddleware {


    @SuppressWarnings("unchecked")
    public static Middleware.IntermediateMiddleware<HttpServletRequest, HttpServletResponse> route(String method, String uriPattern, Middleware.BasicMiddleware<HttpServletRequest, HttpServletResponse> middleware) {
        Route route = new Route(method, uriPattern, middleware);
        return (request, response, next) -> {
            if (request.getAttribute(PATH_PARAMS) == null) {
                request.setAttribute(PATH_PARAMS, new HashMap<String, String>());
            }
            if (route.matches(request.getMethod(), request.getRequestURI(), (Map<String, String>) request.getAttribute(PATH_PARAMS), "")) {
                middleware.run(request, response);
            } else {
                next.run(null);
            }
        };
    }
}

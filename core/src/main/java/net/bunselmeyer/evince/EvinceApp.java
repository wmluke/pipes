package net.bunselmeyer.evince;

import net.bunselmeyer.hitch.App;
import net.bunselmeyer.hitch.middleware.Middleware;

import java.io.IOException;

public interface EvinceApp<Q, P> extends App<Q, P> {

    EvinceApp get(String uriPattern, Middleware.BasicMiddleware<Q, P> middleware);

    EvinceApp post(String uriPattern, Middleware.BasicMiddleware<Q, P> middleware);

    EvinceApp put(String uriPattern, Middleware.BasicMiddleware<Q, P> middleware);

    EvinceApp delete(String uriPattern, Middleware.BasicMiddleware<Q, P> middleware);

    void dispatch(Q req, P res, String contextPath) throws IOException;

    //Stream<Route> routes(Q request);

    //Stream<Route> routes(Q req, String contextPath);

}

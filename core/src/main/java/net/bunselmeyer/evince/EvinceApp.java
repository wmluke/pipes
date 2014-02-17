package net.bunselmeyer.evince;

import net.bunselmeyer.hitch.App;

import java.io.IOException;

public interface EvinceApp<Q, P> extends App<Q, P> {

    MiddlewarePipeline<Q, P> get(String uriPattern);

    MiddlewarePipeline<Q, P> post(String uriPattern);

    MiddlewarePipeline<Q, P> put(String uriPattern);

    MiddlewarePipeline<Q, P> delete(String uriPattern);

    void dispatch(Q req, P res, String contextPath) throws IOException;
}

package net.bunselmeyer.middleware.pipes;

import net.bunselmeyer.middleware.core.AbstractApp;
import net.bunselmeyer.middleware.core.App;
import net.bunselmeyer.middleware.core.Next;
import net.bunselmeyer.middleware.core.RoutableApp;
import net.bunselmeyer.middleware.core.middleware.Middleware;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;

import java.util.Iterator;

public class Pipes extends AbstractApp<HttpRequest, HttpResponse, Pipes> implements App<HttpRequest, HttpResponse, Pipes>, RoutableApp<HttpRequest, HttpResponse> {

    public static Pipes create(Pipes app) {
        return create().use(app);
    }

    public static Pipes create() {
        return new Pipes();
    }

    private Pipes() {

    }

    @Override
    protected Next buildStack(HttpRequest req, HttpResponse res, Iterator<Middleware<HttpRequest, HttpResponse>> stack) {
        return new PipesNext(stack, req, res);
    }

    @Override
    protected void toJson(HttpResponse res, Object memo) {
        if (memo instanceof String) {
            res.json((String) memo);
        } else {
            res.toJson(memo);
        }
    }

    @Override
    public MiddlewarePipeline<HttpRequest, HttpResponse> get(String uriPattern) {
        return new RestMiddlewarePipeline(this, "get", uriPattern);
    }

    @Override
    public MiddlewarePipeline<HttpRequest, HttpResponse> post(String uriPattern) {
        return new RestMiddlewarePipeline(this, "post", uriPattern);
    }

    @Override
    public MiddlewarePipeline<HttpRequest, HttpResponse> delete(String uriPattern) {
        return new RestMiddlewarePipeline(this, "delete", uriPattern);
    }

    @Override
    public MiddlewarePipeline<HttpRequest, HttpResponse> put(String uriPattern) {
        return new RestMiddlewarePipeline(this, "put", uriPattern);
    }



}

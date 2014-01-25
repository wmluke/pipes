package net.bunselmeyer.evince;

import net.bunselmeyer.evince.http.HttpRequest;
import net.bunselmeyer.evince.http.HttpResponse;
import net.bunselmeyer.evince.http.servlet.HttpRequestServletAdapter;
import net.bunselmeyer.evince.http.servlet.HttpResponseServletAdapter;
import net.bunselmeyer.evince.middleware.RouteMiddleware;
import net.bunselmeyer.hitch.App;
import net.bunselmeyer.hitch.Hitch;
import net.bunselmeyer.hitch.middleware.Middleware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Consumer;

public class Evince implements EvinceApp<HttpRequest, HttpResponse> {

    private final Hitch hitch;

    public static Evince create(Hitch app) {
        return new Evince(app);
    }

    public static Evince create(Evince app) {
        return create().use(app);
    }

    public static Evince create() {
        return new Evince(Hitch.create());
    }

    private Evince(Hitch hitch) {
        this.hitch = hitch;
    }

    public Hitch hitch() {
        return hitch;
    }

    @Override
    public Configuration configuration() {
        return hitch.configuration();
    }

    @Override
    public Evince configure(Consumer<Configuration> consumer) {
        hitch.configure(consumer);
        return this;
    }

    @Override
    public Evince use(App<HttpRequest, HttpResponse> app) {
        use(app::dispatch);
        return this;
    }

    @Override
    public Evince use(Middleware.AdvancedMiddleware<HttpRequest, HttpResponse> middleware) {
        hitch.use((e, request, response, next) -> {
            middleware.run(e, buildRequest(request), buildResponse(response), next);
        });
        return this;
    }

    @Override
    public Evince use(Middleware.IntermediateMiddleware<HttpRequest, HttpResponse> middleware) {
        hitch.use((request, response, next) -> {
            middleware.run(buildRequest(request), buildResponse(response), next);
        });
        return this;
    }

    @Override
    public Evince use(Middleware.BasicMiddleware<HttpRequest, HttpResponse> middleware) {
        hitch.use((request, response) -> {
            middleware.run(buildRequest(request), buildResponse(response));
        });
        return this;
    }

    @Override
    public Evince get(String uriPattern, Middleware.BasicMiddleware<HttpRequest, HttpResponse> middleware) {
        return use(RouteMiddleware.route("GET", uriPattern, middleware));
    }

    @Override
    public Evince post(String uriPattern, Middleware.BasicMiddleware<HttpRequest, HttpResponse> middleware) {
        return use(RouteMiddleware.route("POST", uriPattern, middleware));
    }

    @Override
    public Evince delete(String uriPattern, Middleware.BasicMiddleware<HttpRequest, HttpResponse> middleware) {
        return use(RouteMiddleware.route("DELETE", uriPattern, middleware));
    }

    @Override
    public Evince put(String uriPattern, Middleware.BasicMiddleware<HttpRequest, HttpResponse> middleware) {
        return use(RouteMiddleware.route("PUT", uriPattern, middleware));
    }

    @Override
    public void dispatch(HttpRequest req, HttpResponse res) throws IOException {
        hitch.dispatch(req.delegate(), res.delegate());
    }


    @Override
    public void dispatch(HttpRequest req, HttpResponse res, String contextPath) throws IOException {

    }

    private HttpResponseServletAdapter buildResponse(HttpServletResponse response) {
        return new HttpResponseServletAdapter(response, configuration().jsonObjectMapper());
    }

    private HttpRequestServletAdapter buildRequest(HttpServletRequest request) {
        return new HttpRequestServletAdapter(request, configuration().jsonObjectMapper(), configuration().xmlObjectMapper());
    }

}

package net.bunselmeyer.evince;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    public <C> Evince configure(Class<C> type, Consumer<C> consumer) throws IllegalAccessException, InstantiationException {
        hitch.configure(type, consumer);
        return this;
    }

    @Override
    public <C> Evince configure(Class<C> type, String name, Consumer<C> consumer) throws IllegalAccessException, InstantiationException {
        hitch.configure(type, name, consumer);
        return this;
    }

    @Override
    public <C> Evince configure(C configuration, Consumer<C> consumer) {
        hitch.configure(configuration, consumer);
        return this;
    }

    @Override
    public <C> Evince configure(C configuration, String name, Consumer<C> consumer) {
        hitch.configure(configuration, name, consumer);
        return this;
    }

    @Override
    public <C> C configuration(Class<C> type) {
        return hitch.configuration(type);
    }

    @Override
    public <C> C configuration(Class<C> type, String name) {
        return hitch.configuration(type, name);
    }

    @Override
    public Evince use(App<HttpRequest, HttpResponse> app) {
        app.use((req1, res1) -> {
            use((req2, res2, next) -> next.run(null));
        });
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
        return new HttpResponseServletAdapter(response, configuration(ObjectMapper.class));
    }

    private HttpRequestServletAdapter buildRequest(HttpServletRequest request) {
        return new HttpRequestServletAdapter(request, configuration(ObjectMapper.class), configuration(ObjectMapper.class, Hitch.XML_MAPPER_NAME));
    }

}

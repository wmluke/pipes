package net.bunselmeyer.middleware.pipes;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bunselmeyer.middleware.core.App;
import net.bunselmeyer.middleware.core.Next;
import net.bunselmeyer.middleware.core.PipesApp;
import net.bunselmeyer.middleware.core.middleware.ExceptionMapperMiddleware;
import net.bunselmeyer.middleware.core.middleware.Middleware;
import net.bunselmeyer.middleware.json.StreamModule;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import net.bunselmeyer.middleware.pipes.http.servlet.HttpRequestServletAdapter;
import net.bunselmeyer.middleware.pipes.http.servlet.HttpResponseServletAdapter;
import net.bunselmeyer.middleware.pipes.http.servlet.ServletApp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Consumer;

public class Pipes implements PipesApp<HttpRequest, HttpResponse, Pipes> {


    public static Pipes create(ServletApp app) {
        return new Pipes(app);
    }

    public static Pipes create(Pipes app) {
        return create().use(app);
    }

    public static Pipes create() {
        return new Pipes(ServletApp.create());
    }

    private final ServletApp app;

    private static ObjectMapper configureObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(new StreamModule());
        return objectMapper;
    }

    private Pipes(ServletApp app) {
        this.app = app;
        try {
            configure(ObjectMapper.class, Pipes::configureObjectMapper);
        } catch (IllegalAccessException | InstantiationException e) {
            // ignore
        }
    }

    public ServletApp servlet() {
        return app;
    }

    @Override
    public <C> Pipes configure(Class<C> type, Consumer<C> consumer) throws IllegalAccessException, InstantiationException {
        app.configure(type, consumer);
        return this;
    }

    @Override
    public <C> Pipes configure(Class<C> type, String name, Consumer<C> consumer) throws IllegalAccessException, InstantiationException {
        app.configure(type, name, consumer);
        return this;
    }

    @Override
    public <C> Pipes configure(C configuration, Consumer<C> consumer) {
        app.configure(configuration, consumer);
        return this;
    }

    @Override
    public <C> Pipes configure(C configuration, String name, Consumer<C> consumer) {
        app.configure(configuration, name, consumer);
        return this;
    }

    @Override
    public <C> C configuration(Class<C> type) {
        return app.configuration(type);
    }

    @Override
    public <C> C configuration(Class<C> type, String name) {
        return app.configuration(type, name);
    }

    @Override
    public Pipes use(App<HttpRequest, HttpResponse, ?> app) {
        use(app::dispatch);
        return this;
    }

    @Override
    public Pipes use(Middleware.StandardMiddleware1<HttpRequest, HttpResponse> middleware) {
        app.use((request, response) -> {
            middleware.run(buildRequest(request), buildResponse(response));
        });
        return this;
    }

    @Override
    public <M> Pipes use(Middleware.StandardMiddleware2<HttpRequest, HttpResponse, M> middleware) {
        app.<M>use((request, response) -> {
            return middleware.run(buildRequest(request), buildResponse(response));
        });
        return this;

    }

    @Override
    public <M, N> Pipes use(Middleware.StandardMiddleware3<HttpRequest, HttpResponse, M, N> middleware) {
        app.<M, N>use((memo, request, response) -> {
            return middleware.run(memo, buildRequest(request), buildResponse(response));
        });
        return this;
    }

    @Override
    public Pipes use(Middleware.StandardMiddleware4<HttpRequest, HttpResponse> middleware) {
        app.use((request, response, next) -> {
            middleware.run(buildRequest(request), buildResponse(response), next);
        });
        return this;
    }

    @Override
    public <M> Pipes use(Middleware.StandardMiddleware5<HttpRequest, HttpResponse, M> middleware) {
        app.<M>use((memo, request, response, next) -> {
            middleware.run(memo, buildRequest(request), buildResponse(response), next);
        });
        return this;
    }

    @Override
    public Pipes onError(Middleware.ExceptionMiddleware<HttpRequest, HttpResponse> middleware) {
        app.onError((e, request, response, next) -> {
            middleware.run(e, buildRequest(request), buildResponse(response), next);
        });
        return this;
    }

    @Override
    public <E extends Throwable> Pipes onError(Class<E> exceptionType, Middleware.CheckedExceptionMiddleware<HttpRequest, HttpResponse, E> middleware) {
        onError(ExceptionMapperMiddleware.handleException(exceptionType, middleware));
        return this;
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

    @Override
    public void dispatch(HttpRequest req, HttpResponse res, Next n) throws IOException {
        use((req1, res1, next) -> {
            Object memo = next.memo();
            if (memo != null) {
                if (memo instanceof String) {
                    res1.json((String) memo);
                } else {
                    res1.toJson(memo);
                }
            }
        });

        app.dispatch(req.delegate(), res.delegate(), n);
    }


    @Override
    public void dispatch(HttpRequest req, HttpResponse res, String contextPath) throws IOException {

    }

    private HttpResponseServletAdapter buildResponse(HttpServletResponse response) {
        return new HttpResponseServletAdapter(response, configuration(ObjectMapper.class));
    }

    private HttpRequestServletAdapter buildRequest(HttpServletRequest request) {
        return new HttpRequestServletAdapter(request, configuration(ObjectMapper.class), configuration(ObjectMapper.class, ServletApp.XML_MAPPER_NAME));
    }

}

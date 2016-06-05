package net.bunselmeyer.middleware.pipes.middleware;

import net.bunselmeyer.middleware.core.PipesApp;
import net.bunselmeyer.middleware.core.middleware.Middleware;
import net.bunselmeyer.middleware.pipes.Pipes;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import net.bunselmeyer.middleware.pipes.persistence.Persistence;
import net.bunselmeyer.middleware.pipes.persistence.Repository;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface RestfulControllerMiddleware<Q, P> {

    RestfulControllerMiddleware<Q, P> create(String uriPattern, Consumer<PipesApp.MiddlewarePipeline<Q, P>> consumer);

    RestfulControllerMiddleware<Q, P> read(String uriPattern, Consumer<PipesApp.MiddlewarePipeline<Q, P>> consumer);

    RestfulControllerMiddleware<Q, P> index(String uriPattern, Consumer<PipesApp.MiddlewarePipeline<Q, P>> consumer);

    RestfulControllerMiddleware<Q, P> update(String uriPattern, Consumer<PipesApp.MiddlewarePipeline<Q, P>> consumer);

    RestfulControllerMiddleware<Q, P> delete(String uriPattern, Consumer<PipesApp.MiddlewarePipeline<Q, P>> consumer);

    RestfulControllerMiddleware<Q, P> error(Middleware.ExceptionMiddleware<Q, P> middleware);

    <E extends Throwable> RestfulControllerMiddleware<Q, P> error(Class<E> exceptionType, Middleware.CheckedExceptionMiddleware<Q, P, E> middleware);

    public static <M> Pipes restfulController(Class<M> modelType, Persistence persistence, BiConsumer<RestfulControllerMiddleware<HttpRequest, HttpResponse>, Repository<M>> consumer) {

        Pipes app = Pipes.create();

        consumer.accept(RestfulControllerMiddleware.build(app), persistence.build(modelType));

        return app;

    }

    public static <Q, P> RestfulControllerMiddleware<Q, P> build(final PipesApp<Q, P, ?> app) {
        return new RestfulControllerMiddleware<Q, P>() {
            @Override
            public RestfulControllerMiddleware<Q, P> create(String uriPattern, Consumer<PipesApp.MiddlewarePipeline<Q, P>> consumer) {
                consumer.accept(app.post(uriPattern));
                return this;
            }

            @Override
            public RestfulControllerMiddleware<Q, P> read(String uriPattern, Consumer<PipesApp.MiddlewarePipeline<Q, P>> consumer) {
                consumer.accept(app.get(uriPattern));
                return this;
            }

            @Override
            public RestfulControllerMiddleware<Q, P> index(String uriPattern, Consumer<PipesApp.MiddlewarePipeline<Q, P>> consumer) {
                consumer.accept(app.get(uriPattern));
                return this;
            }

            @Override
            public RestfulControllerMiddleware<Q, P> update(String uriPattern, Consumer<PipesApp.MiddlewarePipeline<Q, P>> consumer) {
                consumer.accept(app.put(uriPattern));
                return this;
            }

            @Override
            public RestfulControllerMiddleware<Q, P> delete(String uriPattern, Consumer<PipesApp.MiddlewarePipeline<Q, P>> consumer) {
                consumer.accept(app.delete(uriPattern));
                return this;
            }

            @Override
            public RestfulControllerMiddleware<Q, P> error(Middleware.ExceptionMiddleware<Q, P> middleware) {
                app.onError(middleware);
                return this;
            }

            @Override
            public <E extends Throwable> RestfulControllerMiddleware<Q, P> error(Class<E> exceptionType, Middleware.CheckedExceptionMiddleware<Q, P, E> middleware) {
                app.onError(exceptionType, middleware);
                return this;
            }
        };
    }
}

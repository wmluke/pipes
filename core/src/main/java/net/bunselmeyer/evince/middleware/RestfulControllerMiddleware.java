package net.bunselmeyer.evince.middleware;

import net.bunselmeyer.evince.Evince;
import net.bunselmeyer.evince.EvinceApp;
import net.bunselmeyer.evince.http.HttpRequest;
import net.bunselmeyer.evince.http.HttpResponse;
import net.bunselmeyer.evince.persistence.Persistence;
import net.bunselmeyer.evince.persistence.Repository;
import net.bunselmeyer.hitch.App;
import net.bunselmeyer.hitch.middleware.Middleware;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface RestfulControllerMiddleware<Q, P> {

    RestfulControllerMiddleware<Q, P> create(String uriPattern, Consumer<App.MiddlewarePipeline<Q, P>> consumer);

    RestfulControllerMiddleware<Q, P> read(String uriPattern, Consumer<App.MiddlewarePipeline<Q, P>> consumer);

    RestfulControllerMiddleware<Q, P> index(String uriPattern, Consumer<App.MiddlewarePipeline<Q, P>> consumer);

    RestfulControllerMiddleware<Q, P> update(String uriPattern, Consumer<App.MiddlewarePipeline<Q, P>> consumer);

    RestfulControllerMiddleware<Q, P> delete(String uriPattern, Consumer<App.MiddlewarePipeline<Q, P>> consumer);

    RestfulControllerMiddleware<Q, P> error(Middleware.ExceptionMiddleware<Q, P> middleware);

    <E extends Throwable> RestfulControllerMiddleware<Q, P> error(Class<E> exceptionType, Middleware.CheckedExceptionMiddleware<Q, P, E> middleware);

    public static <M> Evince restfulController(Class<M> modelType, Persistence persistence, BiConsumer<RestfulControllerMiddleware<HttpRequest, HttpResponse>, Repository<M>> consumer) {

        Evince app = Evince.create();

        consumer.accept(RestfulControllerMiddleware.build(app), persistence.build(modelType));

        return app;

    }

    public static <Q, P> RestfulControllerMiddleware<Q, P> build(final EvinceApp<Q, P> app) {
        return new RestfulControllerMiddleware<Q, P>() {
            @Override
            public RestfulControllerMiddleware<Q, P> create(String uriPattern, Consumer<App.MiddlewarePipeline<Q, P>> consumer) {
                consumer.accept(app.post(uriPattern));
                return this;
            }

            @Override
            public RestfulControllerMiddleware<Q, P> read(String uriPattern, Consumer<App.MiddlewarePipeline<Q, P>> consumer) {
                consumer.accept(app.get(uriPattern));
                return this;
            }

            @Override
            public RestfulControllerMiddleware<Q, P> index(String uriPattern, Consumer<App.MiddlewarePipeline<Q, P>> consumer) {
                consumer.accept(app.get(uriPattern));
                return this;
            }

            @Override
            public RestfulControllerMiddleware<Q, P> update(String uriPattern, Consumer<App.MiddlewarePipeline<Q, P>> consumer) {
                consumer.accept(app.put(uriPattern));
                return this;
            }

            @Override
            public RestfulControllerMiddleware<Q, P> delete(String uriPattern, Consumer<App.MiddlewarePipeline<Q, P>> consumer) {
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

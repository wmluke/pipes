package net.bunselmeyer.middleware.pipes.middleware;

import net.bunselmeyer.middleware.core.Next;
import net.bunselmeyer.middleware.core.PipesApp;
import net.bunselmeyer.middleware.pipes.Pipes;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;

import javax.ws.rs.Path;
import java.io.IOException;
import java.util.function.Consumer;

public abstract class RestfullController {

    private void bind(PipesApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipeline, Consumer<PipesApp.MiddlewarePipeline<HttpRequest, HttpResponse>> consumer) {
        consumer.accept(pipeline);
    }

    public void dispatch(HttpRequest req, HttpResponse res, Next next) throws IOException, NoSuchMethodException {
        Pipes app = Pipes.create();

        configure(app);
        bind(app.post(getPath("create")), this::create);
        bind(app.get(getPath("read")), this::read);
        bind(app.get(getPath("index")), this::index);
        bind(app.put(getPath("update")), this::update);
        bind(app.delete(getPath("delete")), this::delete);
        onError(app);

        app.dispatch(req, res, next);
    }

    private String getPath(String method) throws NoSuchMethodException {
        //Path rootPath = getClass().getDeclaredAnnotation(Path.class);
        return getClass().getDeclaredMethod(method, PipesApp.MiddlewarePipeline.class)
            .getDeclaredAnnotation(Path.class)
            .value();
    }

    protected void configure(Pipes app) {

    }

    protected void onError(Pipes app) {

    }

    protected abstract void create(PipesApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipeline);

    protected abstract void read(PipesApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipeline);

    protected abstract void index(PipesApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipeline);

    protected abstract void update(PipesApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipeline);

    protected abstract void delete(PipesApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipeline);
}

package net.bunselmeyer.middleware.pipes.middleware;

import net.bunselmeyer.middleware.core.ConfigurableApp;
import net.bunselmeyer.middleware.core.Next;
import net.bunselmeyer.middleware.core.RoutableApp;
import net.bunselmeyer.middleware.core.RunnableApp;
import net.bunselmeyer.middleware.pipes.Pipes;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.ws.rs.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class RestfullController implements RunnableApp<HttpRequest, HttpResponse> {

    protected void configure(ConfigurableApp app) {

    }

    protected void create(RoutableApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipeline) {

    }

    protected void read(RoutableApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipeline) {

    }

    protected void index(RoutableApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipeline) {

    }

    protected void update(RoutableApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipeline) {

    }

    protected void delete(RoutableApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipeline) {

    }

    protected void onError(Pipes app) {

    }

    @Override
    public final void run(HttpRequest req, HttpResponse res, Next next) throws Exception {
        Pipes app = Pipes.create();
        configure(app);
        bind(app::post, "create", this::create);
        bind(app::get, "read", this::read);
        bind(app::get, "index", this::index);
        bind(app::put, "update", this::update);
        bind(app::delete, "delete", this::delete);
        onError(app);
        app.run(req, res, next);
    }

    private void bind(Function<String, RoutableApp.MiddlewarePipeline<HttpRequest, HttpResponse>> pipelineFunction, String method, Consumer<RoutableApp.MiddlewarePipeline<HttpRequest, HttpResponse>> consumer) {
        String path = getPath(method);
        if (StringUtils.isNotBlank(path)) {
            consumer.accept(pipelineFunction.apply(path));
        }
    }

    private String getPath(String method) {
        try {
            String path = getClass()
                .getDeclaredMethod(method, RoutableApp.MiddlewarePipeline.class)
                .getDeclaredAnnotation(Path.class)
                .value();
            return Paths.get(getRootPath(), path).toString();
        } catch (NoSuchMethodException | NullPointerException e) {
            return null;
        }
    }

    @Nonnull
    private String getRootPath() {
        try {
            return StringUtils.trimToEmpty(getClass().getDeclaredAnnotation(Path.class).value());
        } catch (NullPointerException e) {
            return "";
        }
    }
}

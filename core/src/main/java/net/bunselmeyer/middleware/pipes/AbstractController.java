package net.bunselmeyer.middleware.pipes;

import net.bunselmeyer.middleware.core.Next;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;

public abstract class AbstractController implements Controller<Pipes> {

    private final Pipes app;

    public AbstractController(Pipes app) {
        this.app = app;
    }

    public AbstractController() {
        this.app = Pipes.create();
    }

    public final void initialize() throws InstantiationException, IllegalAccessException {
        configure(app);
        middleware(app);
        route(app);
        onError(app);
    }

    @Override
    public final void run(HttpRequest req, HttpResponse res, Next next) throws Exception {
        app.run(req, res, next);
    }
}

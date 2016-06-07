package net.bunselmeyer.middleware.pipes;

import net.bunselmeyer.middleware.core.Next;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import net.bunselmeyer.middleware.pipes.persistence.Persistence;
import org.hibernate.cfg.Configuration;

public abstract class AbstractController implements Controller<Pipes> {

    private final Pipes app;

    private boolean initialized;

    public AbstractController(Pipes app) {
        this.app = app;
    }

    public AbstractController() {
        this.app = Pipes.create();
    }

    @Override
    public <C> C configuration(Class<C> type) {
        return app.configuration(type);
    }

    @Override
    public <C> C configuration(Class<C> type, String name) {
        return app.configuration(type, name);
    }

    public final void initialize() {
        configure(app);
        if (app.configuration(Configuration.class) != null)
            configurePersistence(app.configuration(Configuration.class));
        middleware(app);
        route(app);
        onError(app);
        initialized = true;
    }

    protected void configurePersistence(Configuration hibernateConfig) {
        app.configure(Persistence.class,
            () -> {
                return Persistence.create(hibernateConfig);
            },
            (persistence) -> {
            });
    }

    @Override
    public final void run(HttpRequest req, HttpResponse res, Next next) throws Exception {
        if (!initialized) initialize();

        app.run(req, res, next);
    }
}

package net.bunselmeyer.hitch.app;

import net.bunselmeyer.hitch.middleware.Middleware;
import net.bunselmeyer.hitch.middleware.MiddlewareFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class AbstractApp<Q, P> implements App<Q, P> {

    private final Configuration configuration = new AppConfiguration();
    protected final List<Route> routes = new ArrayList<>();

    @Override
    public App<Q, P> configure(Consumer<Configuration> consumer) {
        consumer.accept(configuration);
        return this;
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    @Override
    public App<Q, P> use(App<Q, P> app) {
        routes.add(new Route(null, "/**", (Middleware.BasicMiddleware<Q, P>) app::dispatch));
        return this;
    }

    @Override
    public <T extends Middleware> App<Q, P> use(MiddlewareFactory<T> middlewareFactory) {
        routes.add(new Route(null, "/**", middlewareFactory.get()));
        return this;
    }

    @Override
    public App<Q, P> use(Middleware.BasicMiddleware<Q, P> middleware) {
        routes.add(new Route(null, "/**", middleware));
        return this;
    }

    @Override
    public App<Q, P> use(Middleware.IntermediateMiddleware<Q, P> middleware) {
        routes.add(new Route(null, "/**", middleware));
        return this;
    }

    @Override
    public App<Q, P> use(Middleware.AdvancedMiddleware<Q, P> middleware) {
        routes.add(new Route(null, "/**", middleware));
        return this;
    }

    @Override
    public App<Q, P> get(String uriPattern, Middleware.BasicMiddleware<Q, P> middleware) {
        routes.add(new Route("GET", uriPattern, middleware));
        return this;
    }

    @Override
    public App<Q, P> post(String uriPattern, Middleware.BasicMiddleware<Q, P> middleware) {
        routes.add(new Route("POST", uriPattern, middleware));
        return this;
    }

    @Override
    public App<Q, P> put(String uriPattern, Middleware.BasicMiddleware<Q, P> middleware) {
        routes.add(new Route("PUT", uriPattern, middleware));
        return this;
    }

    @Override
    public App<Q, P> delete(String uriPattern, Middleware.BasicMiddleware<Q, P> middleware) {
        routes.add(new Route("DELETE", uriPattern, middleware));
        return this;
    }

    @Override
    public Stream<Route> routes(Q req) {
        return routes(req, "");
    }

    @Override
    public void dispatch(Q req, P res) throws IOException {
        final Iterator<Route> stack = routes(req).iterator();
        Middleware.Next next = buildNext(stack, req, res);
        next.run(null);
    }

    @Override
    public void dispatch(Q req, P res, String contextPath) throws IOException {
        final Iterator<Route> stack = routes(req, contextPath).iterator();
        Middleware.Next next = buildNext(stack, req, res);
        next.run(null);
    }

    protected abstract AbstractNext<Q, P> buildNext(Iterator<Route> stack, Q req, P res);


    public static abstract class AbstractNext<Q, P> implements Middleware.Next {

        private final Iterator<Route> stack;
        private final Q req;
        private final P res;

        public AbstractNext(Iterator<Route> stack, Q req, P res) {
            this.stack = stack;
            this.req = req;
            this.res = res;
        }

        protected abstract void handleException(Exception err);

        protected abstract void handleNotFound();

        protected abstract boolean isCommitted();

        @Override
        public void run(Exception err) {

            if (isCommitted()) {
                return;
            }

            if (!stack.hasNext()) {
                if (err != null) {
                    handleException(err);

                } else {
                    handleNotFound();
                }
            }

            Route route = stack.next();
            runner(route.middleware(), err, req, res, this);
        }

        @SuppressWarnings("unchecked")
        protected void runner(Middleware middleware, Exception err, Q req, P res, Middleware.Next next) {
            try {
                if (err != null) {
                    if (middleware instanceof Middleware.AdvancedMiddleware) {
                        ((Middleware.AdvancedMiddleware<Q, P>) middleware).run(err, req, res, next);
                    } else {
                        next.run(err);
                    }

                } else if (middleware instanceof Middleware.IntermediateMiddleware) {
                    ((Middleware.IntermediateMiddleware<Q, P>) middleware).run(req, res, next);

                } else if (middleware instanceof Middleware.BasicMiddleware) {
                    ((Middleware.BasicMiddleware<Q, P>) middleware).run(req, res);
                    next.run(null);

                } else {
                    next.run(null);
                }
            } catch (Exception e) {
                next.run(e);
            }
        }
    }

}

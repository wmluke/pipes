package net.bunselmeyer.hitch;

import net.bunselmeyer.hitch.middleware.Middleware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class Hitch implements App<HttpServletRequest, HttpServletResponse> {

    private static final Logger logger = LoggerFactory.getLogger(Hitch.class);

    private final Configuration configuration = new AppConfiguration();
    private final List<Middleware> middlewares = new ArrayList<>();

    public static Hitch create() {
        return new Hitch();
    }

    public static Hitch create(Hitch app) {
        Hitch hitch = new Hitch();
        hitch.use(app);
        return hitch;
    }

    protected Hitch() {
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    @Override
    public Hitch configure(Consumer<Configuration> consumer) {
        consumer.accept(configuration);
        return this;
    }

    @Override
    public Hitch use(App<HttpServletRequest, HttpServletResponse> app) {
        return use(app::dispatch);
    }

    @Override
    public Hitch use(Middleware.BasicMiddleware<HttpServletRequest, HttpServletResponse> middleware) {
        middlewares.add(middleware);
        return this;
    }

    @Override
    public Hitch use(Middleware.AdvancedMiddleware<HttpServletRequest, HttpServletResponse> middleware) {
        middlewares.add(middleware);
        return this;
    }

    @Override
    public Hitch use(Middleware.IntermediateMiddleware<HttpServletRequest, HttpServletResponse> middleware) {
        middlewares.add(middleware);
        return this;
    }

    @Override
    public void dispatch(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Iterator<Middleware> stack = middlewares.iterator();
        AbstractNext<HttpServletRequest, HttpServletResponse> next = buildNext(stack, req, res);
        next.run(null);
    }

    protected AbstractNext<HttpServletRequest, HttpServletResponse> buildNext(Iterator<Middleware> stack, HttpServletRequest req, HttpServletResponse res) {
        return new AbstractNext<HttpServletRequest, HttpServletResponse>(stack, req, res) {
            @Override
            protected void handleException(Exception err) {
                sendError(500, "Internal server error");
                logger.error(err.getMessage());
                throw new RuntimeException(err);
            }

            @Override
            protected void handleNotFound() {
                sendError(404, "404 Not found");

            }

            @Override
            protected boolean isCommitted() {
                return res.isCommitted();
            }

            private void sendError(int status, String body) {
                res.setContentType("text/html");
                res.setCharacterEncoding("UTF-8");
                try {
                    res.sendError(status, body);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static abstract class AbstractNext<Q, P> implements Middleware.Next {

        private final Iterator<Middleware> stack;
        private final Q req;
        private final P res;

        public AbstractNext(Iterator<Middleware> stack, Q req, P res) {
            this.stack = stack;
            this.req = req;
            this.res = res;
        }

        protected abstract void handleException(Exception err);

        protected abstract void handleNotFound();

        protected abstract boolean isCommitted();

        @Override
        public final void run(Exception err) {

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

            Middleware middleware = stack.next();
            App.runner(middleware, err, req, res, this);
        }
    }
}

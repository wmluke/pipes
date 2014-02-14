package net.bunselmeyer.hitch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import net.bunselmeyer.hitch.middleware.Middleware;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class Hitch implements App<HttpServletRequest, HttpServletResponse> {

    private static final Logger logger = LoggerFactory.getLogger(Hitch.class);

    public static final String XML_MAPPER_NAME = "xml-mapper";

    private final List<Middleware> middlewares = new ArrayList<>();
    private final Map<String, Object> configs = new HashMap<>();

    public static Hitch create() {
        return new Hitch();
    }

    public static Hitch create(Hitch app) {
        Hitch hitch = create();
        hitch.use(app);
        return hitch;
    }

    protected Hitch() {
        configs.put(getKey(ObjectMapper.class, null), new ObjectMapper());
        configs.put(getKey(ObjectMapper.class, XML_MAPPER_NAME), new ObjectMapper());
    }

    private <S> String getKey(Class<S> type, String name) {
        List<String> names = new ArrayList<>();
        names.add(type.getName());
        if (StringUtils.isNotBlank(name)) {
            names.add(StringUtils.stripToNull(name));
        }
        return Joiner.on(":").skipNulls().join(names);
    }

    @Override
    public <S> S configuration(Class<S> type) {
        return configuration(type, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> S configuration(Class<S> type, String name) {
        return (S) configs.get(getKey(type, name));
    }

    @Override
    public <S> Hitch configure(S configuration, Consumer<S> consumer) {
        configure(configuration, null, consumer);
        return this;
    }

    @Override
    public <S> App<HttpServletRequest, HttpServletResponse> configure(S configuration, String name, Consumer<S> consumer) {
        configs.put(getKey(configuration.getClass(), name), configuration);
        consumer.accept(configuration);
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> Hitch configure(Class<S> type, String name, Consumer<S> consumer) throws IllegalAccessException, InstantiationException {
        String key = getKey(type, name);
        if (configs.get(key) == null) {
            configs.put(key, type.newInstance());
        }
        consumer.accept((S) configs.get(key));
        return this;
    }

    @Override
    public <S> Hitch configure(Class<S> type, Consumer<S> consumer) throws IllegalAccessException, InstantiationException {
        configure(type, null, consumer);
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

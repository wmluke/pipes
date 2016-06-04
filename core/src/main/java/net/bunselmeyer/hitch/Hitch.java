package net.bunselmeyer.hitch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import net.bunselmeyer.hitch.middleware.AbstractNext;
import net.bunselmeyer.hitch.middleware.ExceptionMapperMiddleware;
import net.bunselmeyer.hitch.middleware.Middleware;
import net.bunselmeyer.hitch.middleware.Next;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.session.HashSessionManager;
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

    private final List<Middleware<HttpServletRequest, HttpServletResponse>> middlewares = new ArrayList<>();
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
        configs.put(getKey(HashSessionManager.class, null), new HashSessionManager());
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
        app.use((req1, res1) -> {
            use((req2, res2, next) -> {
                next.run(null);
            });
        });
        use(app::dispatch);
        return this;
    }

    @Override
    public Hitch use(Middleware.StandardMiddleware1<HttpServletRequest, HttpServletResponse> middleware) {
        middlewares.add(middleware);
        return this;
    }

    @Override
    public <M> App<HttpServletRequest, HttpServletResponse> use(Middleware.StandardMiddleware2<HttpServletRequest, HttpServletResponse, M> middleware) {
        middlewares.add(middleware);
        return this;
    }

    @Override
    public <M, N> App<HttpServletRequest, HttpServletResponse> use(Middleware.StandardMiddleware3<HttpServletRequest, HttpServletResponse, M, N> middleware) {
        middlewares.add(middleware);
        return this;
    }

    @Override
    public Hitch use(Middleware.StandardMiddleware4<HttpServletRequest, HttpServletResponse> middleware) {
        middlewares.add(middleware);
        return this;
    }

    @Override
    public <M> App<HttpServletRequest, HttpServletResponse> use(Middleware.StandardMiddleware5<HttpServletRequest, HttpServletResponse, M> middleware) {
        middlewares.add(middleware);
        return this;
    }


    @Override
    public Hitch onError(Middleware.ExceptionMiddleware<HttpServletRequest, HttpServletResponse> middleware) {
        middlewares.add(middleware);
        return this;
    }

    @Override
    public <E extends Throwable> App<HttpServletRequest, HttpServletResponse> onError(Class<E> exceptionType, Middleware.CheckedExceptionMiddleware<HttpServletRequest, HttpServletResponse, E> middleware) {
        onError(ExceptionMapperMiddleware.handleException(exceptionType, middleware));
        return this;
    }

    @Override
    public void dispatch(HttpServletRequest req, HttpServletResponse res, Next next) throws IOException {
        Iterator<Middleware<HttpServletRequest, HttpServletResponse>> stack = middlewares.iterator();
        AbstractNext<HttpServletRequest, HttpServletResponse> n = buildNext(stack, req, res);
        n.run(next != null ? next.memo() : null);
    }

    private AbstractNext<HttpServletRequest, HttpServletResponse> buildNext(Iterator<Middleware<HttpServletRequest, HttpServletResponse>> stack, HttpServletRequest req, HttpServletResponse res) {
        return new AbstractNext<HttpServletRequest, HttpServletResponse>(stack, req, res) {
            @Override
            protected void handleException(Throwable err) {
                sendError(500, "Internal server error");
                err.printStackTrace();
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

}

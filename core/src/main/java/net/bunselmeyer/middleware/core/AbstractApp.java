package net.bunselmeyer.middleware.core;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import net.bunselmeyer.middleware.core.middleware.ExceptionMapperMiddleware;
import net.bunselmeyer.middleware.core.middleware.Middleware;
import net.bunselmeyer.middleware.json.StreamModule;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.session.HashSessionManager;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractApp<Q, P, A extends AbstractApp<Q, P, ?>> implements App<Q, P, A> {

    private final List<Middleware<Q, P>> middlewares = new ArrayList<>();
    private final Map<String, Object> configs = new HashMap<>();

    private static ObjectMapper configureObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(new StreamModule());
        return objectMapper;
    }

    public AbstractApp() {
        configs.put(getKey(SessionManager.class, null), new HashSessionManager());
        configs.put(getKey(ObjectMapper.class, null), configureObjectMapper(new ObjectMapper()));
        configs.put(getKey(ObjectMapper.class, XML_MAPPER_NAME), configureObjectMapper(new ObjectMapper()));
    }

    protected abstract Next buildStack(Q req, P res, Iterator<Middleware<Q, P>> stack);

    protected abstract void toJson(P res, Object memo);

    @Override
    public <S> S configuration(Class<S> type) {
        return configuration(type, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> S configuration(Class<S> type, String name) {
        return (S) configs.get(getKey(type, name));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> A configure(S configuration, Consumer<S> consumer) {
        configure(configuration, null, consumer);
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> A configure(S configuration, String name, Consumer<S> consumer) {
        configs.put(getKey(configuration.getClass(), name), configuration);
        consumer.accept(configuration);
        return (A) this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> A configure(Class<S> type, String name, Consumer<S> consumer) {
        String key = getKey(type, name);
        try {
            configs.putIfAbsent(key, type.newInstance());
            consumer.accept((S) configs.get(key));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C, S extends C> A configure(Class<C> type, Supplier<S> supplier, Consumer<S> consumer) {
        configure(type, supplier, "", consumer);
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C, S extends C> A configure(Class<C> type, Supplier<S> supplier, String name, Consumer<S> consumer) {
        String key = getKey(type, name);
        configs.putIfAbsent(key, supplier.get());
        consumer.accept((S) configs.get(key));
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> A configure(Class<S> type, Consumer<S> consumer) {
        configure(type, "", consumer);
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public A use(Middleware.StandardMiddleware1<Q, P> middleware) {
        middlewares.add(middleware);
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <M> A use(Middleware.StandardMiddleware2<Q, P, M> middleware) {
        middlewares.add(middleware);
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <M, N> A use(Middleware.StandardMiddleware3<Q, P, M, N> middleware) {
        middlewares.add(middleware);
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public A use(Middleware.StandardMiddleware4<Q, P> middleware) {
        middlewares.add(middleware);
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <M> A use(Middleware.StandardMiddleware5<Q, P, M> middleware) {
        middlewares.add(middleware);
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public A onError(Middleware.ExceptionMiddleware<Q, P> middleware) {
        middlewares.add(middleware);
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Throwable> A onError(Class<E> exceptionType, Middleware.CheckedExceptionMiddleware<Q, P, E> middleware) {
        onError(ExceptionMapperMiddleware.handleException(exceptionType, middleware));
        return (A) this;
    }

    @Override
    public void run(Q req, P res, Next next) throws Exception {
        use((req1, res1, next1) -> {
            Object memo = next1.memo();
            if (memo != null && !(memo instanceof Throwable)) {
                toJson(res1, memo);
                return;
            }
            next1.run(memo);
        });
        Next n = buildStack(req, res, Collections.unmodifiableList(new ArrayList<>(middlewares)).iterator());
        n.run(next != null ? next.memo() : null);
    }

    private <S> String getKey(Class<S> type, String name) {
        List<String> names = new ArrayList<>();
        names.add(type.getName());
        if (StringUtils.isNotBlank(name)) {
            names.add(StringUtils.stripToNull(name));
        }
        return Joiner.on(":").skipNulls().join(names);
    }
}

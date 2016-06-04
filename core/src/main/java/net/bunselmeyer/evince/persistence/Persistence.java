package net.bunselmeyer.evince.persistence;

import net.bunselmeyer.hitch.middleware.Middleware;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public interface Persistence {

    <M> Repository<M> build(Class<M> type);

    <T> T transaction(boolean readOnly, UnitOfWorkWithResult<T> unitOfWork) throws Exception;

    void transaction(boolean readOnly, UnitOfWork unitOfWork) throws Exception;

    <Q, P> Middleware.StandardMiddleware1<Q, P> transactional(boolean readOnly, Middleware.StandardMiddleware1<Q, P> middleware);

    <Q, P, M> Middleware.StandardMiddleware2<Q, P, M> transactional(boolean readOnly, Middleware.StandardMiddleware2<Q, P, M> middleware);

    <Q, P, M, N> Middleware.StandardMiddleware3<Q, P, M, N> transactional(boolean readOnly, Middleware.StandardMiddleware3<Q, P, M, N> middleware);

    <Q, P> Middleware.StandardMiddleware4<Q, P> transactional(boolean readOnly, Middleware.StandardMiddleware4<Q, P> middleware);

    <Q, P, M> Middleware.StandardMiddleware5<Q, P, M> transactional(boolean readOnly, Middleware.StandardMiddleware5<Q, P, M> middleware);


    @FunctionalInterface
    public static interface UnitOfWork {
        void run() throws Exception;
    }

    @FunctionalInterface
    public static interface UnitOfWorkWithResult<T> {
        T run() throws Exception;
    }

    public static Persistence create(Configuration configuration) {
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties())
            .buildServiceRegistry();
        return new PersistenceImpl(configuration.buildSessionFactory(serviceRegistry));
    }
}

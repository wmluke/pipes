package net.bunselmeyer.evince.persistence;

import net.bunselmeyer.hitch.middleware.Middleware;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public interface Persistence {

    <M> Repository<M> build(Class<M> type);

    <T> T transaction(boolean readOnly, UnitOfWorkWithResult<T> unitOfWork) throws Exception;

    void transaction(boolean readOnly, UnitOfWork unitOfWork) throws Exception;

    <Q, P> Middleware.BasicMiddleware<Q, P> transactional(boolean readOnly, Middleware.BasicMiddleware<Q, P> middleware);

    <Q, P> Middleware.IntermediateMiddleware<Q, P> transactional(boolean readOnly, Middleware.IntermediateMiddleware<Q, P> middleware);

    @FunctionalInterface
    public static interface UnitOfWork {
        void run() throws Exception;
    }

    @FunctionalInterface
    public static interface UnitOfWorkWithResult<T> {
        T run() throws Exception;
    }

    public static Persistence create(Configuration configuration) {
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        return new PersistenceImpl(configuration.buildSessionFactory(serviceRegistry));
    }
}

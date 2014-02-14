package net.bunselmeyer.evince.persistence;

import net.bunselmeyer.hitch.middleware.Middleware;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.function.Supplier;

public interface Persistence {

    <M> Repository<M> build(Class<M> type);

    <T> T transaction(boolean readOnly, Supplier<T> unitOfWork);

    void transaction(boolean readOnly, Runnable unitOfWork);

    <Q, P> Middleware.BasicMiddleware<Q, P> transactional(boolean readOnly, Middleware.BasicMiddleware<Q, P> middleware);

    public static Persistence create(Configuration configuration) {
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        return new PersistenceImpl(configuration.buildSessionFactory(serviceRegistry));
    }
}

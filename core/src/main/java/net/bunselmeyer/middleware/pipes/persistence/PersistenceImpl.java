package net.bunselmeyer.middleware.pipes.persistence;

import net.bunselmeyer.middleware.core.middleware.Middleware;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class PersistenceImpl implements Persistence {

    private final SessionFactory sessionFactory;

    PersistenceImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <M> Repository<M> build(final Class<M> type) {
        return new SimpleRepository<M>(sessionFactory::getCurrentSession, type);
    }

    @Override
    public <M, R extends SimpleRepository<M>> R build(final Class<M> type, Class<R> repository) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return repository.getConstructor(Supplier.class, Class.class)
            .newInstance((Supplier) sessionFactory::getCurrentSession, type);
    }

    @Override
    public <T> T transaction(boolean readOnly, UnitOfWorkWithResult<T> unitOfWork) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        if (session.getTransaction().isActive()) {
            return unitOfWork.run();
        } else {
            Transaction transaction = session.beginTransaction();
            session.setDefaultReadOnly(readOnly);
            try {
                T result = unitOfWork.run();
                transaction.commit();
                return result;
            } finally {
                if (!transaction.wasCommitted()) {
                    transaction.rollback();
                }
            }
        }
    }

    @Override
    public void transaction(boolean readOnly, UnitOfWork unitOfWork) throws Exception {
        transaction(readOnly, () -> {
            unitOfWork.run();
            return null;
        });
    }

    @Override
    public <Q, P> Middleware.StandardMiddleware1<Q, P> transactional(boolean readOnly, Middleware.StandardMiddleware1<Q, P> middleware) {
        return (req, res) -> transaction(readOnly, () -> {
            middleware.run(req, res);
        });
    }

    @Override
    public <Q, P, M> Middleware.StandardMiddleware2<Q, P, M> transactional(boolean readOnly, Middleware.StandardMiddleware2<Q, P, M> middleware) {
        return (req, res) -> transaction(readOnly, () -> {
            return middleware.run(req, res);
        });
    }

    @Override
    public <Q, P, M, N> Middleware.StandardMiddleware3<Q, P, M, N> transactional(boolean readOnly, Middleware.StandardMiddleware3<Q, P, M, N> middleware) {
        return (memo, req, res) -> transaction(readOnly, () -> {
            return middleware.run(memo, req, res);
        });

    }

    @Override
    public <Q, P> Middleware.StandardMiddleware4<Q, P> transactional(boolean readOnly, Middleware.StandardMiddleware4<Q, P> middleware) {
        return (req, res, next) -> transaction(readOnly, () -> {
            middleware.run(req, res, next);
        });
    }

    @Override
    public <Q, P, M> Middleware.StandardMiddleware5<Q, P, M> transactional(boolean readOnly, Middleware.StandardMiddleware5<Q, P, M> middleware) {
        return (memo, req, res, next) -> transaction(readOnly, () -> {
            middleware.run(memo, req, res, next);
        });
    }
}

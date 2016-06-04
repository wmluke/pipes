package net.bunselmeyer.evince.persistence;

import net.bunselmeyer.hitch.middleware.Middleware;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PersistenceImpl implements Persistence {

    private final SessionFactory sessionFactory;

    PersistenceImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <M> Repository<M> build(final Class<M> type) {
        return new AbstractRepository<M>() {
            @Override
            protected Session getCurrentSession() {
                return sessionFactory.getCurrentSession();
            }

            @Override
            protected Class<M> getPersistentClass() {
                return type;
            }
        };
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

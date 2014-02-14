package net.bunselmeyer.evince.persistence;

import net.bunselmeyer.hitch.middleware.Middleware;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.Supplier;

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
    public <T> T transaction(boolean readOnly, Supplier<T> unitOfWork) {
        Session session = sessionFactory.getCurrentSession();
        if (session.getTransaction().isActive()) {
            return unitOfWork.get();
        } else {
            Transaction transaction = session.beginTransaction();
            session.setDefaultReadOnly(readOnly);
            try {
                T result = unitOfWork.get();
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
    public void transaction(boolean readOnly, Runnable unitOfWork) {
        transaction(readOnly, () -> {
            unitOfWork.run();
            return null;
        });
    }

    @Override
    public <Q, P> Middleware.BasicMiddleware<Q, P> transactional(boolean readOnly, Middleware.BasicMiddleware<Q, P> middleware) {
        return (req, res) -> transaction(readOnly, () -> {
            try {
                middleware.run(req, res);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}

package net.bunselmeyer.evince.middleware;

import net.bunselmeyer.evince.http.HttpRequest;
import net.bunselmeyer.evince.http.HttpResponse;
import net.bunselmeyer.hitch.middleware.Middleware;
import org.hibernate.Session;
import org.hibernate.Transaction;


public class TransactionMiddleware {


    public static Middleware.IntermediateMiddleware<HttpRequest, HttpResponse> unitOfWork(Session session, boolean readOnly) {


        return (req, res, next) -> {

        };

    }

    public abstract class UnitOfWork<T> {

        public abstract T unitOfWork(final Session session) throws Throwable;

        public final T execute(final Session session, boolean readOnly) throws Throwable {

            if (session.getTransaction().isActive()) {
                return unitOfWork(session);
            } else {
                Transaction transaction = session.beginTransaction();
                session.setDefaultReadOnly(readOnly);
                try {
                    T result = unitOfWork(session);
                    transaction.commit();
                    return result;
                } finally {
                    if (!transaction.wasCommitted()) {
                        transaction.rollback();
                    }
                }
            }
        }
    }


}

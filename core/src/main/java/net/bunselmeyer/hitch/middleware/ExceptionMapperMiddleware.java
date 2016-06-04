package net.bunselmeyer.hitch.middleware;

public class ExceptionMapperMiddleware {

    @SuppressWarnings("unchecked")
    public static <Q, P, E extends Throwable> Middleware.ExceptionMiddleware<Q, P> handleException(Class<E> type, Middleware.CheckedExceptionMiddleware<Q, P, E> middleware) {
        return (e, req, res, next) -> {
            if (e != null && type.isAssignableFrom(e.getClass())) {
                middleware.run((E) e, req, res, next);
                return;
            }
            next.run(e);
        };
    }

}

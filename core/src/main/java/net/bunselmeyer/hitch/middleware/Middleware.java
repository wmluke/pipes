package net.bunselmeyer.hitch.middleware;

public interface Middleware {

    @FunctionalInterface
    public static interface BasicMiddleware<Q, P> extends Middleware {

        void run(Q req, P resp) throws Exception;

    }

    @FunctionalInterface
    public static interface IntermediateMiddleware<Q, P> extends Middleware {

        void run(Q req, P resp, Next next) throws Exception;

    }

    @FunctionalInterface
    public static interface ExceptionMiddleware<Q, P> extends CheckedExceptionMiddleware<Q, P, Exception> {

        void run(Exception e, Q req, P resp, Next next) throws Exception;

    }

    @FunctionalInterface
    interface CheckedExceptionMiddleware<Q, P, E extends Throwable> extends Middleware {

        void run(E e, Q req, P resp, Next next) throws Exception;

    }

    @FunctionalInterface
    public static interface Next {

        void run(Exception e);

    }

}

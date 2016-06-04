package net.bunselmeyer.hitch.middleware;

public interface Middleware<Q, P> {

    default void accept(Next next, Object memo) {
        next.visit(this, memo);
    }


    @FunctionalInterface
    interface StandardMiddleware1<Q, P> extends Middleware<Q, P> {

        void run(Q req, P resp) throws Exception;

        default void accept(Next next, Object memo) {
            next.visit(this, memo);
        }
    }

    @FunctionalInterface
    interface StandardMiddleware2<Q, P, M> extends Middleware<Q, P> {

        M run(Q req, P resp) throws Exception;

        default void accept(Next next, Object memo) {
            next.visit(this, memo);
        }

    }

    @FunctionalInterface
    interface StandardMiddleware3<Q, P, M, N> extends Middleware<Q, P> {

        N run(M memo, Q req, P resp) throws Exception;

        default void accept(Next next, Object memo) {
            next.visit(this, memo);
        }

    }

    @FunctionalInterface
    interface StandardMiddleware4<Q, P> extends Middleware<Q, P> {

        void run(Q req, P resp, Next next) throws Exception;

        default void accept(Next next, Object memo) {
            next.visit(this, memo);
        }

    }

    @FunctionalInterface
    interface StandardMiddleware5<Q, P, M> extends Middleware<Q, P> {

        void run(M memo, Q req, P resp, Next next) throws Exception;

        default void accept(Next next, Object memo) {
            next.visit(this, memo);
        }

    }

    @FunctionalInterface
    interface ExceptionMiddleware<Q, P> extends Middleware<Q, P> {

        void run(Exception memo, Q req, P resp, Next next) throws Exception;

        default void accept(Next next, Object memo) {
            next.visit(this, memo);
        }

    }

    @FunctionalInterface
    interface CheckedExceptionMiddleware<Q, P, E extends Throwable> extends Middleware<Q, P> {

        void run(E memo, Q req, P resp, Next next) throws Exception;

        default void accept(Next next, Object memo) {
            next.visit(this, memo);
        }

    }

}

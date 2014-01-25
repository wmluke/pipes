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
    public static interface AdvancedMiddleware<Q, P> extends Middleware {

        void run(Exception e, Q req, P resp, Next next) throws Exception;

    }

    @FunctionalInterface
    public static interface Next {

        void run(Exception e);

    }

}

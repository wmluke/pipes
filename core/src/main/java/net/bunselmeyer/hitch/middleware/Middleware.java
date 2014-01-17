package net.bunselmeyer.hitch.middleware;

import net.bunselmeyer.hitch.http.HttpRequest;
import net.bunselmeyer.hitch.http.HttpResponse;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Consumer;


public interface Middleware {


    @FunctionalInterface
    public static interface ServletMiddleware extends Middleware {

        void run(HttpServletRequest req, HttpServletResponse res) throws Exception;
    }

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

    public static IntermediateMiddleware<HttpRequest, HttpResponse> logger(Logger logger, Consumer<LoggerMiddleware.Options> block) {
        return LoggerMiddleware.logger(logger, block);
    }
}

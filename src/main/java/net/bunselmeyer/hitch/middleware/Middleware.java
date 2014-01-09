package net.bunselmeyer.hitch.middleware;

import net.bunselmeyer.hitch.http.HttpRequest;
import net.bunselmeyer.hitch.http.HttpResponse;
import org.slf4j.Logger;

import java.util.Date;
import java.util.Map;


public interface Middleware {

    public static IntermediateMiddleware requestLogger(Logger logger, boolean detailed) {
        return (res, resp, next) -> {
            Date start = new Date();
            next.run(null);
            long duration = new Date().getTime() - start.getTime();
            logger.info(res.method() + " " + res.uri() + "?" + res.query() + " " + resp.status() + " " + duration + "msec");
            if (detailed) {
                logger.info("  HEADERS:");
                for (Map.Entry<String, String> entry : res.headers().entrySet()) {
                    logger.info("    " + entry.getKey() + ": " + entry.getValue());
                }
            }
        };
    }

    @FunctionalInterface
    public static interface BasicMiddleware extends Middleware {

        void run(HttpRequest req, HttpResponse resp) throws Exception;

    }

    @FunctionalInterface
    public static interface IntermediateMiddleware extends Middleware {

        void run(HttpRequest req, HttpResponse resp, Next next) throws Exception;

    }

    @FunctionalInterface
    public static interface AdvancedMiddleware extends Middleware {

        void run(Exception e, HttpRequest req, HttpResponse resp, Next next) throws Exception;

    }

    @FunctionalInterface
    public static interface Next {

        void run(Exception e);
    }

}

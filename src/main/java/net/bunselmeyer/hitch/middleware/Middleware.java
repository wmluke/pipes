package net.bunselmeyer.hitch.middleware;

import io.netty.handler.codec.http.Cookie;
import net.bunselmeyer.hitch.http.HttpRequest;
import net.bunselmeyer.hitch.http.HttpResponse;
import org.slf4j.Logger;

import java.util.Map;


public interface Middleware {

    public static BasicMiddleware requestLogger(Logger logger) {
        return (res, resp) -> {
            logger.info("REQUEST: " + res.method() + " " + res.uri());
            logger.info("  HEADERS:");
            for (Map.Entry<String, String> entry : res.headers().entrySet()) {
                logger.info("    " + entry.getKey() + ": " + entry.getValue());
            }
            logger.info("  COOKIES:");
            for (Map.Entry<String, Cookie> entry : res.cookies().entrySet()) {
                logger.info("    " + entry.getKey() + ": " + entry.getValue().toString());
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

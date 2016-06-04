package net.bunselmeyer.hitch.middleware;

import net.bunselmeyer.evince.http.HttpRequest;
import net.bunselmeyer.evince.http.HttpResponse;
import org.slf4j.Logger;

import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

public class LoggerMiddleware {

    public static Middleware.StandardMiddleware4<HttpRequest, HttpResponse> logger(Logger logger, Consumer<Options> block) {
        Options options = new Options();
        block.accept(options);
        return (req, res, next) -> {
            Date start = new Date();
            next.run(null);
            long duration = new Date().getTime() - start.getTime();
            logger.info("\n\nREQUEST " + req.method() + " " + req.uri() + "?" + req.query() + " " + res.status() + " " + duration + "msec");
            if (options.logHeaders) {
                logger.info("  HEADERS:");
                for (Map.Entry<String, String> entry : req.headers().entrySet()) {
                    logger.info("    " + entry.getKey() + ": " + entry.getValue());
                }
            }

            logger.info("RESPONSE");
            if (options.logHeaders) {
                logger.info("  HEADERS:");
                for (Map.Entry<String, String> entry : res.headers().entrySet()) {
                    logger.info("    " + entry.getKey() + ": " + entry.getValue());
                }
            }
        };
    }

    public static class Options {

        /**
         * Log headers
         */
        public boolean logHeaders;

    }
}

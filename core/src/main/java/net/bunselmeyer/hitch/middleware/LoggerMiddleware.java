package net.bunselmeyer.hitch.middleware;

import org.slf4j.Logger;

import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

public class LoggerMiddleware {

    public static Middleware.IntermediateMiddleware logger(Logger logger, Consumer<Options> block) {
        Options options = new Options();
        block.accept(options);
        return (res, resp, next) -> {
            Date start = new Date();
            next.run(null);
            long duration = new Date().getTime() - start.getTime();
            logger.info(res.method() + " " + res.uri() + "?" + res.query() + " " + resp.status() + " " + duration + "msec");
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

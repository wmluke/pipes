package net.bunselmeyer.hitch.app;

import io.netty.handler.codec.http.Cookie;
import org.slf4j.Logger;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;

public interface Middleware {

    void run(Request req, Response resp) throws IOException, ServletException;

    public static Middleware requestLogger(Logger logger) {
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

}

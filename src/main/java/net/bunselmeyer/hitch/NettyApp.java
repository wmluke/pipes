package net.bunselmeyer.hitch;

import io.netty.handler.codec.http.Cookie;
import net.bunselmeyer.hitch.app.App;
import net.bunselmeyer.hitch.app.HttpServer;
import net.bunselmeyer.hitch.app.Middleware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NettyApp {

    private static final Logger logger = LoggerFactory.getLogger(NettyApp.class);

    public static final Middleware REQUEST_LOGGER = (res, resp) -> {
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

    public static void main(String[] args) throws Exception {

        App app = App.create();

        app.use(REQUEST_LOGGER);

        app.use((req, resp) -> {
            resp.charset("UTF-8");
            resp.type("text/html");
        });

        app.use((req, resp) -> {
            resp.cookie("foo", "bar", (cookie) -> {
                cookie.setPath("/");
                cookie.setHttpOnly(true);
            });
            resp.send(200, "<h1>hello world!</h1>");
        });

        HttpServer.createNettyHttpServer(app).listen(8888);

    }
}

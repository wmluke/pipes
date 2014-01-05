package net.bunselmeyer.hitch;

import net.bunselmeyer.hitch.app.App;
import net.bunselmeyer.hitch.app.HttpServer;
import net.bunselmeyer.hitch.app.Middleware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyApp {

    private static final Logger logger = LoggerFactory.getLogger(JettyApp.class);

    public static void main(String[] args) throws Exception {

        App app = App.create();

        app.use(Middleware.requestLogger(logger));

        app.use((req, resp) -> {
            resp.charset("UTF-8");
            resp.type("text/html");
        });

        app.use((req, resp) -> {
            if (req.uri().startsWith("/restricted")) {
                resp.send(401, "Restricted Area");
            }
        });

        app.use((req, resp) -> {
            resp.cookie("foo", "bar", (cookie) -> {
                cookie.setPath("/");
                cookie.setHttpOnly(true);
            });
            resp.send(200, "<h1>hello world!</h1>");
        });

        HttpServer.createJettyServer(app).listen(8888);

    }
}

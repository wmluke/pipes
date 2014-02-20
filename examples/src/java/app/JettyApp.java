package app;

import app.configure.HibernateConfig;
import app.configure.JacksonJsonConfig;
import app.configure.LogbackConfig;
import app.configure.SessionManagerConfig;
import app.models.User;
import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import net.bunselmeyer.evince.Evince;
import net.bunselmeyer.evince.persistence.Persistence;
import net.bunselmeyer.server.HttpServer;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static app.middleware.SimpleControllerMiddleware.simpleController;
import static net.bunselmeyer.evince.middleware.MountResourceMiddleware.Evince.mountResourceDir;
import static net.bunselmeyer.hitch.middleware.LoggerMiddleware.logger;

public class JettyApp {

    private static final Logger logger = LoggerFactory.getLogger(JettyApp.class);

    public static void main(String[] args) throws Exception {

        Evince app = Evince.create();

        app.configure(HashSessionManager.class, SessionManagerConfig::configure);
        app.configure(ObjectMapper.class, JacksonJsonConfig::configure);
        app.configure((LoggerContext) LoggerFactory.getILoggerFactory(), LogbackConfig::configure);
        app.configure(org.hibernate.cfg.Configuration.class, HibernateConfig::configure);

        Persistence persistence = Persistence.create(app.configuration(Configuration.class));

        app.use(logger(logger, (opts) -> opts.logHeaders = true));

        // Start a session
        app.use((req, res) -> {
            req.delegate().getSession();
        });

        app.use((req, res) -> {
            res.charset("UTF-8");
            res.type("text/html");
        });

        /**
         * Mount multiple resource folders (/assets1, /assets2) to a single URI path (/assets)
         */

        app.use(mountResourceDir("/assets1", "/assets", (options) -> {
            options.handleNotFound = false; // allow missing files to be handled by the next middleware
        }));

        app.use(mountResourceDir("/assets2", "/assets"));


        app.use((req, res) -> {
            if (req.uri().startsWith("/restricted")) {
                res.send(401, "Restricted Area");
            }
        });

        app.use((req, res) -> {
            res.cookie("foo", "bar", (cookie) -> {
                cookie.setPath("/");
                cookie.setHttpOnly(true);
            });
        });

        app.get("/").pipe((req, res) -> {
            res.send(200, "<h1>hello world!</h1>");
        });

        app.get("/locations/{country}/{state}/{city}").pipe((req, res) -> {
            String country = req.routeParam("country");
            String state = req.routeParam("state");
            String city = req.routeParam("city");
            res.send(200, "<h1>" + Joiner.on(", ").join(country, state, city) + "</h1>");
        });

        app.get("/error").pipe((req, res) -> {
            throw new RuntimeException("Fail!");
        });

        app.post("/").pipe((req, res) -> {
            String aaa = req.body().asFormUrlEncoded().get("aaa").get(0);
            String bbb = req.body().asFormUrlEncoded().get("bbb").get(0);
            res.send(200, "<p>" + aaa + ", " + bbb + "</p>");
        });

        app.post("/foo").pipe((req, res) -> {
            JsonNode jsonNode = req.body().asJson();
            res.json(200, jsonNode.toString());
        });

        app.use(simpleController(User.class, persistence));

        app.use((err, req, res, next) -> {
            if (err != null) {
                res.send(400, "Handled error: " + err.getMessage());
                return;
            }
            next.run(null);
        });

        int port = 8888;

        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        HttpServer.createJettyServer(app).listen(port);

    }

}

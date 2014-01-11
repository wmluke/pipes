package net.bunselmeyer.hitch.examples;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import net.bunselmeyer.hitch.app.App;
import net.bunselmeyer.hitch.http.HttpServer;
import net.bunselmeyer.hitch.middleware.BodyTransformers;
import net.bunselmeyer.hitch.middleware.Middleware;
import net.bunselmeyer.hitch.transport.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;


public class JettyApp {

    private static final Logger logger = LoggerFactory.getLogger(JettyApp.class);


    public static void configure(LoggerContext loggerContext) {
        loggerContext.reset();

        StatusManager sm = loggerContext.getStatusManager();
        if (sm != null) {
            sm.add(new InfoStatus("Setting up default Hitch configuration.", loggerContext));
        }

        ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<>();
        ca.setContext(loggerContext);
        ca.setWithJansi(true);
        ca.setName("console");

        PatternLayoutEncoder pl = new PatternLayoutEncoder();
        pl.setCharset(Charset.forName("UTF-8"));
        pl.setContext(loggerContext);
        //pl.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        pl.setPattern("%msg%n");
        pl.start();

        ca.setEncoder(pl);
        ca.start();
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(ca);
    }

    public static void main(String[] args) throws Exception {

        App app = App.create();

        app.configure((config) -> {
            JsonUtil.configureJsonObjectMapper(config.jsonObjectMapper());

            configure(config.loggerContext());
        });

        app.use(Middleware.requestLogger(logger, true));

        app.use((req, res) -> {
            res.charset("UTF-8");
            res.type("text/html");
        });

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

        app.get("/", (req, res) -> {
            res.send(200, "<h1>hello world!</h1>");
        });

        app.get("/locations/{country}/{state}/{city}", (req, res) -> {
            String country = req.routeParam("country");
            String state = req.routeParam("state");
            String city = req.routeParam("city");
            res.send(200, "<h1>" + Joiner.on(", ").join(country, state, city) + "</h1>");
        });

        app.get("/error", (req, res) -> {
            throw new RuntimeException("Fail!");
        });

        app.post("/", (req, res) -> {
            String aaa = req.body().asFormUrlEncoded().get("aaa").get(0);
            String bbb = req.body().asFormUrlEncoded().get("bbb").get(0);
            res.send(200, "<p>" + aaa + ", " + bbb + "</p>");
        });

        app.post("/foo", (req, res) -> {
            JsonNode jsonNode = req.body().asJson();
            res.json(200, jsonNode.toString());
        });

        app.post("/user", BodyTransformers.json(User.class));
        app.post("/user", (req, res) -> {
            User user = req.body().asTransformed();
            res.send("<p>" + user.getFirstName() + " " + user.getLastName() + "</p>");
        });

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

    public static class User {

        private String firstName;
        private String lastName;

        public String getFirstName() {
            return firstName;
        }

        public User setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public String getLastName() {
            return lastName;
        }

        public User setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }
    }
}

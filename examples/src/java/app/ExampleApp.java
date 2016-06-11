package app;


import app.configure.HibernateConfig;
import app.configure.JacksonJsonConfig;
import app.configure.LogbackConfig;
import app.configure.SessionManagerConfig;
import app.controller.UserController;
import app.exceptions.RecordNotFoundException;
import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import net.bunselmeyer.middleware.core.ConfigurableApp;
import net.bunselmeyer.middleware.core.MiddlewareApp;
import net.bunselmeyer.middleware.core.RoutableApp;
import net.bunselmeyer.middleware.pipes.AbstractController;
import net.bunselmeyer.middleware.pipes.Pipes;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import net.bunselmeyer.middleware.pipes.http.HttpSession;
import net.bunselmeyer.middleware.pipes.middleware.RequestBody;
import net.bunselmeyer.middleware.pipes.persistence.Persistence;
import net.bunselmeyer.middleware.util.Optionals;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static net.bunselmeyer.middleware.pipes.middleware.Assets.mountResourceDir;
import static net.bunselmeyer.middleware.pipes.middleware.RequestLogger.logger;

public class ExampleApp extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(ExampleApp.class);

    public ExampleApp() throws IllegalAccessException, InstantiationException {
        super();
    }

    @Override
    public void configure(ConfigurableApp<Pipes> app) {
        app.configure(SessionManager.class, HashSessionManager::new, SessionManagerConfig::configure);
        app.configure(ObjectMapper.class, JacksonJsonConfig::configure);
        app.configure((LoggerContext) LoggerFactory.getILoggerFactory(), LogbackConfig::configure);
        app.configure(org.hibernate.cfg.Configuration.class, HibernateConfig::configure);
    }

    @Override
    public void middleware(MiddlewareApp<HttpRequest, HttpResponse, Pipes> app) {
        app.use(logger(logger, (opts) -> opts.logHeaders = true));

        // Start a session
        app.use((req, res) -> {
            req.session(true).ifPresent((session) -> {
                session.put("abc", "def");
            });
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

    }

    @Override
    public void route(RoutableApp<HttpRequest, HttpResponse> app) {
        app.get("/").pipe((req, res) -> {
            res.send(200, "<h1>hello world!</h1>");
        });


        app.get("/stream")
            .pipe((req1, res1) -> {
                return Stream.of("one", "two", "three");
            })
            .pipe((memo, req, res) -> {
                return memo.map(String::length);
            });

        app.get("/locations/{country}/{state}/{city}").pipe((req, res) -> {
            String country = req.routeParam("country").orElse("");
            String state = req.routeParam("state").orElse("");
            String city = req.routeParam("city").orElse("");
            res.send(200, "<h1>" + Joiner.on(", ").join(country, state, city) + "</h1>");
        });

        app.get("/error").pipe((req, res) -> {
            return new RuntimeException("Fail!");
        });

        app.post("/").pipe((req, res) -> {
            Map<String, List<String>> parameters = req.body().asFormUrlEncoded();
            String aaa = parameters.get("aaa").get(0);
            String bbb = parameters.get("bbb").get(0);
            res.send(200, "<p>" + aaa + ", " + bbb + "</p>");
        });

        app.post("/foo").pipe((req, res) -> {
            JsonNode jsonNode = req.body().fromJson();
            res.toJson(200, jsonNode.toString());
        });

        app.put("/session/{id}")
            .pipe(RequestBody.fromJson(Map.class))
            .pipe((data, req, res) -> {
                res.send(Optionals
                    .map(req.session(), req.routeParam("id").asOptional(), (session, id) -> {
                        session.put(id, data);
                        return 201;
                    })
                    .orElse(400));
                return null;
            });

        app.get("/session/{id}")
            .pipe((req, res) -> {
                // Explicit explicit type parameter, `<RecordNotFoundException>`, needed to fix javac bug
                // compilation error: `unreported exception X; must be caught or declared to be thrown`
                // https://gist.github.com/rompetroll/667bf46ac0168a92497a
                // https://bugs.openjdk.java.net/browse/JDK-8056983
                return Optionals
                    .map(req.session(), req.routeParam("id").asOptional(), HttpSession::get)
                    .<RecordNotFoundException>orElseThrow(RecordNotFoundException::new)
                    .get();
            })
            .pipe((req, res, next) -> {
                res.toJson(200, next.memo());
            });

    }

    @Override
    public void onError(Pipes app) {

        app.onError((err, req, res, next) -> {
            if (err != null) {
                err.printStackTrace();

                res.send(400, "Handled error: " + err.getMessage());
                return;
            }
            next.run(null);
        });

        Persistence persistence = app.configuration(Persistence.class);
        app.use(new UserController(persistence));
    }
}

package net.bunselmeyer.hitch.app;

import net.bunselmeyer.hitch.http.HttpRequest;
import net.bunselmeyer.hitch.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.stream.Stream;

public class Evince extends AbstractApp<HttpRequest, HttpResponse> {

    private static final Logger logger = LoggerFactory.getLogger(Evince.class);


    public static Evince create() {
        return new Evince();
    }

    private Evince() {
    }

    @Override
    protected AbstractNext<HttpRequest, HttpResponse> buildNext(Iterator<Route> stack, HttpRequest req, HttpResponse res) {
        return new AbstractNext<HttpRequest, HttpResponse>(stack, req, res) {
            @Override
            protected void handleException(Exception err) {
                if (res.status() < 400) {
                    res.status(500);
                }
                res.type("text/html");
                res.charset("UTF-8");
                res.send("Internal server error");
                logger.error(err.getMessage());
                throw new RuntimeException(err);
            }

            @Override
            protected void handleNotFound() {
                res.send(404, "404 Not found");
            }

            @Override
            protected boolean isCommitted() {
                return res.isCommitted();
            }
        };
    }

    @Override
    public Stream<Route> routes(HttpRequest req, String contextPath) {
        return routes.stream().filter((r) -> r.matches(req.method(), req.uri(), req.routeParams(), contextPath));
    }

}

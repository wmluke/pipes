package net.bunselmeyer.hitch.app;

import net.bunselmeyer.hitch.http.HttpRequest;
import net.bunselmeyer.hitch.http.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
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
        String method = req.method();
        return routes.stream().filter((r) -> {
            // match method
            if (StringUtils.stripToNull(r.method()) != null && !StringUtils.equalsIgnoreCase(r.method(), method)) {
                return false;
            }
            // thank you jersey-common for the uri pattern matching!
            String uri = Paths.get(contextPath, req.uri()).toString();
            return r.uriPattern() == null || r.uriPattern().match(uri, req.routeParams());
        });
    }

}

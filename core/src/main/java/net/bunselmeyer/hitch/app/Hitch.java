package net.bunselmeyer.hitch.app;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Stream;

public class Hitch extends AbstractApp<HttpServletRequest, HttpServletResponse> {

    private static final Logger logger = LoggerFactory.getLogger(Hitch.class);

    public static Hitch create() {
        return new Hitch();
    }

    private Hitch() {
    }

    @Override
    protected AbstractNext<HttpServletRequest, HttpServletResponse> buildNext(Iterator<Route> stack, HttpServletRequest req, HttpServletResponse res) {
        return new AbstractNext<HttpServletRequest, HttpServletResponse>(stack, req, res) {
            @Override
            protected void handleException(Exception err) {
                sendError(500, "Internal server error");
                logger.error(err.getMessage());
                throw new RuntimeException(err);
            }

            @Override
            protected void handleNotFound() {
                sendError(404, "404 Not found");

            }

            @Override
            protected boolean isCommitted() {
                return res.isCommitted();
            }

            private void sendError(int status, String body) {
                res.setContentType("text/html");
                res.setCharacterEncoding("UTF-8");
                try {
                    res.sendError(status, body);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override
    public Stream<Route> routes(HttpServletRequest req, String contextPath) {
        String method = req.getMethod();
        return routes.stream().filter((r) -> {
            // match method
            if (StringUtils.stripToNull(r.method()) != null && !StringUtils.equalsIgnoreCase(r.method(), method)) {
                return false;
            }
            // thank you jersey-common for the uri pattern matching!
            String uri = Paths.get(contextPath, req.getRequestURI()).toString();
            return r.uriPattern() == null || r.uriPattern().match(uri, new HashMap<>());
        });
    }
}

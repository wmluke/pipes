package net.bunselmeyer.middleware.pipes;

import net.bunselmeyer.middleware.core.AbstractNext;
import net.bunselmeyer.middleware.core.middleware.Middleware;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class PipesNext extends AbstractNext<HttpRequest, HttpResponse> {

    private static final Logger logger = LoggerFactory.getLogger(PipesNext.class);

    protected PipesNext(Iterator<Middleware<HttpRequest, HttpResponse>> stack, HttpRequest req, HttpResponse res) {
        super(stack, req, res);
    }

    @Override
    protected void handleException(Throwable err) {
        sendError(500, "Internal server error");
        err.printStackTrace();
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
        res.type("text/html");
        res.charset("UTF-8");
        res.send(status, body);
    }
}

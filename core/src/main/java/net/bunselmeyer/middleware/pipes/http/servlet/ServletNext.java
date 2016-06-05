package net.bunselmeyer.middleware.pipes.http.servlet;

import net.bunselmeyer.middleware.core.AbstractNext;
import net.bunselmeyer.middleware.core.middleware.Middleware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;

class ServletNext extends AbstractNext<HttpServletRequest, HttpServletResponse> {

    private static final Logger logger = LoggerFactory.getLogger(ServletNext.class);

    ServletNext(
        Iterator<Middleware<HttpServletRequest, HttpServletResponse>> stack,
        HttpServletRequest req,
        HttpServletResponse res) {
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
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");
        try {
            res.sendError(status, body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

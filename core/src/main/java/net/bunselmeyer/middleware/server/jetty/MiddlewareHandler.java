package net.bunselmeyer.middleware.server.jetty;

import net.bunselmeyer.middleware.core.App;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class MiddlewareHandler extends AbstractHandler {

    private final App<HttpServletRequest, HttpServletResponse, ?> app;

    MiddlewareHandler(App<HttpServletRequest, HttpServletResponse, ?> app) {
        this.app = app;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            app.run(request, response, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        baseRequest.setHandled(true);
    }
}

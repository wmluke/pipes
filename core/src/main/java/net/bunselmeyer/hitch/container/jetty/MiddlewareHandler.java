package net.bunselmeyer.hitch.container.jetty;

import net.bunselmeyer.hitch.app.App;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MiddlewareHandler extends AbstractHandler {

    private final App<HttpServletRequest, HttpServletResponse> app;

    public MiddlewareHandler(App<HttpServletRequest, HttpServletResponse> app) {
        this.app = app;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        app.dispatch(request, response);
        baseRequest.setHandled(true);
    }
}

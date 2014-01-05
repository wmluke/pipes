package net.bunselmeyer.hitch.jetty;

import net.bunselmeyer.hitch.app.App;
import net.bunselmeyer.hitch.app.Middleware;
import net.bunselmeyer.hitch.app.Response;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MiddlewareHandler extends AbstractHandler {

    private final App app;

    public MiddlewareHandler(App app) {
        this.app = app;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {


        net.bunselmeyer.hitch.app.Request req = new HttpServletWrapperRequest(request);
        Response res = new HttpServletWrapperResponse(response);

        for (Middleware middleware : app.middleware()) {
            if (!response.isCommitted()) {
                middleware.run(req, res);
            }
        }

        baseRequest.setHandled(true);
    }
}

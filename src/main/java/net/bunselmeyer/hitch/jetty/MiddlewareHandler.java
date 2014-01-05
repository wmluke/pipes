package net.bunselmeyer.hitch.jetty;

import net.bunselmeyer.hitch.app.App;
import net.bunselmeyer.hitch.app.Response;
import net.bunselmeyer.hitch.servlet.HttpServletWrapperRequest;
import net.bunselmeyer.hitch.servlet.HttpServletWrapperResponse;
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

        app.routes(req).forEach((route) -> {
            if (!response.isCommitted()) {
                try {
                    route.middleware().run(req, res);
                } catch (Exception e) {
                    // yuk
                    throw new RuntimeException(e);
                }
            }
        });

        baseRequest.setHandled(true);
    }
}

package net.bunselmeyer.hitch.server.jetty;

import net.bunselmeyer.hitch.app.App;
import net.bunselmeyer.hitch.http.HttpRequest;
import net.bunselmeyer.hitch.http.HttpResponse;
import net.bunselmeyer.hitch.server.servlet.HttpRequestServletAdapter;
import net.bunselmeyer.hitch.server.servlet.HttpResponseServletAdapter;
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
        HttpRequest req = new HttpRequestServletAdapter(request, app.configuration().jsonObjectMapper(), app.configuration().xmlObjectMapper());
        HttpResponse res = new HttpResponseServletAdapter(response);

        app.dispatch(req, res);

        baseRequest.setHandled(true);
    }
}

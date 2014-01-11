package net.bunselmeyer.hitch.container.jetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bunselmeyer.hitch.app.App;
import net.bunselmeyer.hitch.container.servlet.HttpRequestServletAdapter;
import net.bunselmeyer.hitch.container.servlet.HttpResponseServletAdapter;
import net.bunselmeyer.hitch.http.HttpRequest;
import net.bunselmeyer.hitch.http.HttpResponse;
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
        ObjectMapper jsonMapper = app.configuration().jsonObjectMapper();

        HttpRequest req = new HttpRequestServletAdapter(request, jsonMapper, app.configuration().xmlObjectMapper());
        HttpResponse res = new HttpResponseServletAdapter(response, jsonMapper);

        app.dispatch(req, res);

        baseRequest.setHandled(true);
    }
}

package net.bunselmeyer.middleware.server.jetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bunselmeyer.middleware.core.App;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import net.bunselmeyer.middleware.pipes.http.servlet.HttpRequestServletAdapter;
import net.bunselmeyer.middleware.pipes.http.servlet.HttpResponseServletAdapter;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class MiddlewareHandler extends AbstractHandler {

    private final App<HttpRequest, HttpResponse, ?> app;

    MiddlewareHandler(App<HttpRequest, HttpResponse, ?> app) {
        this.app = app;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            app.run(buildRequest(request), buildResponse(response), null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        baseRequest.setHandled(true);
    }

    private HttpRequestServletAdapter buildRequest(HttpServletRequest request) {
        return new HttpRequestServletAdapter(request, app.configuration(ObjectMapper.class), app.configuration(ObjectMapper.class, App.XML_MAPPER_NAME));
    }

    private HttpResponseServletAdapter buildResponse(HttpServletResponse response) {
        return new HttpResponseServletAdapter(response, app.configuration(ObjectMapper.class));
    }
}

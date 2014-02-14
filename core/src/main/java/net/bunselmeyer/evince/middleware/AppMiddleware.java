package net.bunselmeyer.evince.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bunselmeyer.evince.http.HttpRequest;
import net.bunselmeyer.evince.http.HttpResponse;
import net.bunselmeyer.evince.http.servlet.HttpRequestServletAdapter;
import net.bunselmeyer.evince.http.servlet.HttpResponseServletAdapter;
import net.bunselmeyer.hitch.App;
import net.bunselmeyer.hitch.Hitch;
import net.bunselmeyer.hitch.middleware.Middleware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppMiddleware {

    public static Middleware.BasicMiddleware<HttpServletRequest, HttpServletResponse> app(App<HttpRequest, HttpResponse> app) {
        return (request, response) -> {
            ObjectMapper jsonMapper = app.configuration(ObjectMapper.class);
            ObjectMapper xmlMapper = app.configuration(ObjectMapper.class, Hitch.XML_MAPPER_NAME);
            app.dispatch(new HttpRequestServletAdapter(request, jsonMapper, xmlMapper), new HttpResponseServletAdapter(response, jsonMapper));
        };
    }
}

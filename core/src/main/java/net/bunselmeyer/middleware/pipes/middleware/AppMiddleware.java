package net.bunselmeyer.middleware.pipes.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bunselmeyer.middleware.core.App;
import net.bunselmeyer.middleware.core.middleware.Middleware;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import net.bunselmeyer.middleware.pipes.http.servlet.HttpRequestServletAdapter;
import net.bunselmeyer.middleware.pipes.http.servlet.HttpResponseServletAdapter;
import net.bunselmeyer.middleware.pipes.http.servlet.ServletApp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppMiddleware {

    public static Middleware.StandardMiddleware4<HttpServletRequest, HttpServletResponse> app(App<HttpRequest, HttpResponse, ?> app) {
        return (request, response, next) -> {
            ObjectMapper jsonMapper = app.configuration(ObjectMapper.class);
            ObjectMapper xmlMapper = app.configuration(ObjectMapper.class, ServletApp.XML_MAPPER_NAME);
            app.run(new HttpRequestServletAdapter(request, jsonMapper, xmlMapper), new HttpResponseServletAdapter(response, jsonMapper), next);
        };
    }
}

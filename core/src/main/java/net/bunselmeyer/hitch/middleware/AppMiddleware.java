package net.bunselmeyer.hitch.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bunselmeyer.hitch.app.App;
import net.bunselmeyer.hitch.container.servlet.HttpRequestServletAdapter;
import net.bunselmeyer.hitch.container.servlet.HttpResponseServletAdapter;
import net.bunselmeyer.hitch.http.HttpRequest;
import net.bunselmeyer.hitch.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppMiddleware {

    public static Middleware.BasicMiddleware<HttpRequest, HttpResponse> hitchApp(App<HttpServletRequest, HttpServletResponse> app) {
        return (request, response) -> app.dispatch(request.delegate(), response.delegate());
    }

    public static Middleware.BasicMiddleware<HttpServletRequest, HttpServletResponse> evinceApp(App<HttpRequest, HttpResponse> app) {
        return (request, response) -> {
            ObjectMapper jsonMapper = app.configuration().jsonObjectMapper();
            ObjectMapper xmlMapper = app.configuration().xmlObjectMapper();
            app.dispatch(new HttpRequestServletAdapter(request, jsonMapper, xmlMapper), new HttpResponseServletAdapter(response, jsonMapper));
        };
    }
}

package net.bunselmeyer.hitch.middleware;

import com.fasterxml.jackson.core.type.TypeReference;
import net.bunselmeyer.evince.http.HttpRequest;
import net.bunselmeyer.evince.http.HttpResponse;

public class BodyTransformers {

    public static Middleware.BasicMiddleware<HttpRequest, HttpResponse> json(Class type) {
        return (req, res) -> {
            req.body().transform(() -> req.body().asJson(type));
        };
    }

    public static Middleware.BasicMiddleware<HttpRequest, HttpResponse> json(TypeReference type) {
        return (req, res) -> {
            req.body().transform(() -> req.body().asJson(type));
        };
    }

    public static Middleware.BasicMiddleware<HttpRequest, HttpResponse> formUrlEncoded() {
        return (req, res) -> {
            req.body().transform(() -> req.body().asFormUrlEncoded());
        };
    }
}

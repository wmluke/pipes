package net.bunselmeyer.hitch.middleware;

import com.fasterxml.jackson.core.type.TypeReference;
import net.bunselmeyer.evince.http.HttpRequest;
import net.bunselmeyer.evince.http.HttpResponse;

import java.util.List;
import java.util.Map;

public class BodyTransformers {

    public static <M> Middleware.StandardMiddleware2<HttpRequest, HttpResponse, M> fromJson(Class<M> type) {
        return (req, res) -> req.body().asJson(type);
    }

    public static <M> Middleware.StandardMiddleware2<HttpRequest, HttpResponse, M> fromJson(TypeReference<M> type) {
        return (req, res) -> req.body().asJson(type);
    }

    public static Middleware.StandardMiddleware2<HttpRequest, HttpResponse, Map<String, List<String>>> fromFormUrlEncoded() {
        return (req, res) -> req.body().asFormUrlEncoded();
    }
}

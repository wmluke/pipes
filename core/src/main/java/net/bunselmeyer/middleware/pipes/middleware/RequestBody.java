package net.bunselmeyer.middleware.pipes.middleware;

import com.fasterxml.jackson.core.type.TypeReference;
import net.bunselmeyer.middleware.core.middleware.Middleware;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;

import java.util.List;
import java.util.Map;

public class RequestBody {

    public static <M> Middleware.StandardMiddleware2<HttpRequest, HttpResponse, M> fromJson(Class<M> type) {
        return (req, res) -> req.body().fromJson(type);
    }

    public static <M> Middleware.StandardMiddleware2<HttpRequest, HttpResponse, M> fromJson(TypeReference<M> type) {
        return (req, res) -> req.body().fromJson(type);
    }

    public static Middleware.StandardMiddleware2<HttpRequest, HttpResponse, Map<String, List<String>>> fromFormUrlEncoded() {
        return (req, res) -> req.body().asFormUrlEncoded();
    }
}

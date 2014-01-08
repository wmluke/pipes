package net.bunselmeyer.hitch.middleware;

import com.fasterxml.jackson.core.type.TypeReference;

public class BodyTransformers {

    public static Middleware.BasicMiddleware json(Class type) {
        return (req, res) -> {
            req.body().transform(() -> req.body().asJson(type));
        };
    }

    public static Middleware.BasicMiddleware json(TypeReference type) {
        return (req, res) -> {
            req.body().transform(() -> req.body().asJson(type));
        };
    }

    public static Middleware.BasicMiddleware formUrlEncoded() {
        return (req, res) -> {
            req.body().transform(() -> req.body().asFormUrlEncoded());
        };
    }
}

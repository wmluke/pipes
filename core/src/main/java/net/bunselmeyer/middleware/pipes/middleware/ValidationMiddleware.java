package net.bunselmeyer.middleware.pipes.middleware;

import net.bunselmeyer.middleware.core.middleware.Middleware;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;

import javax.validation.*;
import java.util.Set;

public class ValidationMiddleware {
    public static <M> Middleware.StandardMiddleware3<HttpRequest, HttpResponse, M, M> validateTransformedBody() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return (memo, req, res) -> {
            Set<ConstraintViolation<M>> constraintViolations = validator.validate(memo);
            if (!constraintViolations.isEmpty()) {
                throw new ConstraintViolationException(constraintViolations);
            }
            return memo;
        };
    }
}

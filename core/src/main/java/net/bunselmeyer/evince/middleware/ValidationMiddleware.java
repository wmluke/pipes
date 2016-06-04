package net.bunselmeyer.evince.middleware;

import net.bunselmeyer.evince.http.HttpRequest;
import net.bunselmeyer.evince.http.HttpResponse;
import net.bunselmeyer.hitch.middleware.Middleware;

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

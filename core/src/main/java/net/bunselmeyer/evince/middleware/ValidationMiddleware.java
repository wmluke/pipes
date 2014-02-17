package net.bunselmeyer.evince.middleware;

import net.bunselmeyer.evince.http.HttpRequest;
import net.bunselmeyer.evince.http.HttpResponse;
import net.bunselmeyer.hitch.middleware.Middleware;

import javax.validation.*;
import java.util.Set;

public class ValidationMiddleware {
    public static <T> Middleware.IntermediateMiddleware<HttpRequest, HttpResponse> validateTransformedBody() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return (req, res, next) -> {
            Set<ConstraintViolation<T>> constraintViolations = validator.validate(req.body().asTransformed());
            next.run(constraintViolations.isEmpty() ? null : new ConstraintViolationException(constraintViolations));
        };
    }
}

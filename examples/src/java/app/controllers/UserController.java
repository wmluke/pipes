package app.controllers;

import app.api.ApiConstraintError;
import app.api.ApiResponse;
import app.exceptions.ApiErrorException;
import app.exceptions.RecordNotFoundException;
import app.models.User;
import net.bunselmeyer.evince.Evince;
import net.bunselmeyer.evince.http.HttpRequest;
import net.bunselmeyer.evince.http.HttpResponse;
import net.bunselmeyer.evince.middleware.ValidationMiddleware;
import net.bunselmeyer.evince.persistence.Persistence;
import net.bunselmeyer.evince.persistence.Repository;
import net.bunselmeyer.hitch.middleware.BodyTransformers;
import net.bunselmeyer.hitch.middleware.Middleware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public static Evince create(Persistence persistence) {

        Repository<User> userRepository = persistence.build(User.class);

        Evince app = Evince.create();

        // CREATE
        app.post("/users")
           .pipe(BodyTransformers.fromJson(User.class))
           .pipe(ValidationMiddleware.validateTransformedBody())
           .pipe(persistence.transactional(false, (req, res) -> {
               User user = req.body().asTransformed();
               userRepository.create(user);
           }))
           .pipe(apiResponse(201));

        // INDEX
        app.get("/users")
           .pipe(persistence.transactional(true, (req, res) -> {
               req.body().transform(() -> userRepository.find().list());
           }))
           .pipe(apiResponse(200));

        // READ
        app.get("/users/{id}")
           .pipe(persistence.transactional(true, (req, res, next) -> {
               User user = userRepository.read(Integer.parseInt(req.routeParam("id")));
               if (user == null) {
                   next.run(new RecordNotFoundException());
               }
               req.body().transform(() -> user);
           }))
           .pipe(apiResponse(200));

        // UPDATE
        app.put("/users/{id}")
           .pipe(BodyTransformers.fromJson(User.class))
           .pipe(ValidationMiddleware.validateTransformedBody())
           .pipe(persistence.transactional(false, (req, res) -> {
               User user = userRepository.read(Integer.parseInt(req.routeParam("id")));
               if (user == null) {
                   throw new RecordNotFoundException();
               }
               User jsonUser = req.body().asTransformed();
               if (jsonUser.getId() != user.getId()) {
                   throw new ApiErrorException(400, "Mismatched entity ID");
               }
               userRepository.update(jsonUser);
               req.body().transform(() -> true);
           }))
           .pipe(apiResponse(200));

        // DELETE
        app.delete("/users/{id}")
           .pipe(persistence.transactional(false, (req, res, next) -> {
               User user = userRepository.read(Integer.parseInt(req.routeParam("id")));
               if (user == null) {
                   next.run(new RecordNotFoundException());
               }
               userRepository.delete(user);
               req.body().transform(() -> true);
           }))
           .pipe(apiResponse(200));

        // ERROR HANDLING

        app.use(ApiErrorException.class, (e, req, res, next) -> {
            res.json(e.getStatusCode(), ApiResponse.error(e));
        });

        app.use(ConstraintViolationException.class, (e, req, res, next) -> {
            Set<? extends ConstraintViolation<?>> violations = e.getConstraintViolations();
            ApiConstraintError error = violations == null || violations.isEmpty() ? new ApiConstraintError(e.getMessage()) : new ApiConstraintError();
            if (violations != null) {
                for (ConstraintViolation<?> violation : violations) {
                    error.addConstraintViolation(violation);
                }
            }
            res.json(error.getCode().httpStatus(), ApiResponse.error(error));
        });


        app.use((e, req, res, next) -> {
            logger.error(e.getMessage(), e);
            res.json(500, ApiResponse.error(500, "Unknown Error"));
        });

        return app;
    }

    public static Middleware.BasicMiddleware<HttpRequest, HttpResponse> apiResponse(int status) {
        return (req, res) -> {
            res.json(status, ApiResponse.body(req.body().asTransformed()));
        };
    }

}

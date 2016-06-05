package app.controller;


import app.api.ApiConstraintError;
import app.api.ApiResponse;
import app.exceptions.ApiErrorException;
import app.exceptions.RecordNotFoundException;
import app.models.User;
import net.bunselmeyer.middleware.core.middleware.Middleware;
import net.bunselmeyer.middleware.pipes.Pipes;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import net.bunselmeyer.middleware.pipes.persistence.Persistence;
import net.bunselmeyer.middleware.pipes.persistence.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

import static net.bunselmeyer.middleware.pipes.middleware.BodyTransformers.fromJson;
import static net.bunselmeyer.middleware.pipes.middleware.ValidationMiddleware.validateMemo;

public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public static Pipes create(Persistence persistence) {

        Class<User> modelType = User.class;
        Repository<User> userRepository = persistence.build(modelType);

        Pipes app = Pipes.create();

        app.post("/users")
            .pipe(fromJson(modelType))
            .pipe(validateMemo())
            .pipe(persistence.transactional(false, (model, req, res) -> {
                return userRepository.create(model);
            }))
            .pipe(apiResponse(201));


        app.get("/users/{id}")
            .pipe(persistence.transactional(true, (req, res) -> {
                User model = userRepository.read(Integer.parseInt(req.routeParam("id")));
                if (model == null) {
                    throw new RecordNotFoundException();
                }
                return model;
            }))
            .pipe(apiResponse(200));


        app.get("/users")
            .pipe(persistence.transactional(true, (req, res) -> {
                return userRepository.find().list().stream();
            }))
            .pipe(apiResponse(200));


        app.put("/users/{id}")
            .pipe(fromJson(modelType))
            .pipe(validateMemo())
            .pipe(persistence.transactional(false, (body, req, res) -> {
                User model = userRepository.read(Integer.parseInt(req.routeParam("id")));
                if (model == null) {
                    throw new RecordNotFoundException();
                }
                if (body.getId() != model.getId()) {
                    throw new ApiErrorException(400, "Mismatched entity ID");
                }
                userRepository.update(body);
                return true;
            }))
            .pipe(apiResponse(200));


        app.delete("/users/{id}")
            .pipe(persistence.transactional(false, (req, res) -> {
                User model = userRepository.read(Integer.parseInt(req.routeParam("id")));
                if (model == null) {
                    throw new RecordNotFoundException();
                }
                userRepository.delete(model);
                return true;
            }))
            .pipe(apiResponse(200));


        app.onError(ApiErrorException.class, (e, req, res, next) -> {
            res.toJson(e.getStatusCode(), ApiResponse.error(e));
        });

        app.onError(ConstraintViolationException.class, (e, req, res, next) -> {
            Set<? extends ConstraintViolation<?>> violations = e.getConstraintViolations();
            ApiConstraintError error = violations == null || violations.isEmpty() ? new ApiConstraintError(e.getMessage()) : new ApiConstraintError();
            if (violations != null) {
                for (ConstraintViolation<?> violation : violations) {
                    error.addConstraintViolation(violation);
                }
            }
            res.toJson(error.getCode().httpStatus(), ApiResponse.error(error));
        });

        app.onError((e, req, res, next) -> {
            logger.error(e.getMessage(), e);
            res.toJson(500, ApiResponse.error(500, "Unknown Error"));
        });

        return app;

    }


    public static Middleware.StandardMiddleware4<HttpRequest, HttpResponse> apiResponse(int status) {
        return (req, res, next) -> {
            res.toJson(status, ApiResponse.body(next.memo()));
        };
    }
}

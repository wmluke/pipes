package app.controller;


import app.api.ApiConstraintError;
import app.api.ApiResponse;
import app.exceptions.ApiErrorException;
import app.exceptions.RecordNotFoundException;
import app.models.User;
import net.bunselmeyer.middleware.core.RoutableApp;
import net.bunselmeyer.middleware.pipes.Pipes;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import net.bunselmeyer.middleware.pipes.middleware.RestfullController;
import net.bunselmeyer.middleware.pipes.persistence.Persistence;
import net.bunselmeyer.middleware.pipes.persistence.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Path;
import java.util.Set;

import static app.api.ApiResponse.toJson;
import static net.bunselmeyer.middleware.pipes.middleware.RequestBody.fromJson;
import static net.bunselmeyer.middleware.pipes.middleware.Validate.validateMemo;

@Path("/users")
public class UserController extends RestfullController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final Repository<User> userRepository;
    private final Persistence persistence;
    private Class<User> modelType = User.class;

    public UserController(Persistence persistence) {
        this.persistence = persistence;
        userRepository = persistence.build(User.class);
    }

    @Path("/")
    @Override
    public void create(RoutableApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipeline) {
        pipeline
            .pipe(fromJson(modelType))
            .pipe(validateMemo())
            .pipe(persistence.transactional(false, (model, req, res) -> {
                return userRepository.create(model);
            }))
            .pipe(toJson(201));
    }

    @Path("/{id}")
    @Override
    public void read(RoutableApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipeline) {
        pipeline
            .pipe(persistence.transactional(true, (req, res) -> {
                User model = userRepository.read(Integer.parseInt(req.routeParam("id")));
                if (model == null) {
                    throw new RecordNotFoundException();
                }
                return model;
            }))
            .pipe(toJson(200));
    }

    @Path("/")
    @Override
    public void index(RoutableApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipeline) {
        pipeline
            .pipe(persistence.transactional(true, (req, res) -> {
                return userRepository.find().list().stream();
            }))
            .pipe(toJson(200));
    }

    @Path("/{id}")
    @Override
    public void update(RoutableApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipeline) {
        pipeline
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
            .pipe(toJson(200));
    }

    @Path("/{id}")
    @Override
    public void delete(RoutableApp.MiddlewarePipeline<HttpRequest, HttpResponse> pipeline) {
        pipeline
            .pipe(persistence.transactional(false, (req, res) -> {
                User model = userRepository.read(Integer.parseInt(req.routeParam("id")));
                if (model == null) {
                    throw new RecordNotFoundException();
                }
                userRepository.delete(model);
                return true;
            }))
            .pipe(toJson(200));
    }

    @Override
    public void onError(Pipes app) {
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
    }

}
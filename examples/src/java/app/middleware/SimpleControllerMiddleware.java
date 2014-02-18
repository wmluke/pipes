package app.middleware;

import app.api.ApiConstraintError;
import app.api.ApiResponse;
import app.exceptions.ApiErrorException;
import app.exceptions.RecordNotFoundException;
import app.models.Model;
import net.bunselmeyer.evince.Evince;
import net.bunselmeyer.evince.http.HttpRequest;
import net.bunselmeyer.evince.http.HttpResponse;
import net.bunselmeyer.evince.middleware.RestfulControllerMiddleware;
import net.bunselmeyer.evince.middleware.ValidationMiddleware;
import net.bunselmeyer.evince.persistence.Persistence;
import net.bunselmeyer.hitch.middleware.BodyTransformers;
import net.bunselmeyer.hitch.middleware.Middleware;
import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

public class SimpleControllerMiddleware {

    public static <M extends Model> Evince simpleController(Class<M> modelType, Persistence persistence) {

        String modelName = modelType.getSimpleName();
        String rootUrl = "/" + English.plural(modelName).toLowerCase();

        final Logger logger = LoggerFactory.getLogger(modelName + "SimpleController");

        return RestfulControllerMiddleware.restfulController(modelType, persistence, (controller, repository) -> {

            controller.create(rootUrl, (pipeline) -> {
                pipeline.pipe(BodyTransformers.fromJson(modelType))
                        .pipe(ValidationMiddleware.validateTransformedBody())
                        .pipe(persistence.transactional(false, (req, res) -> {
                            M model = req.body().asTransformed();
                            repository.create(model);
                        }))
                        .pipe(apiResponse(201));

            });

            controller.read(rootUrl + "/{id}", (pipeline) -> {
                pipeline.pipe(persistence.transactional(true, (req, res, next) -> {
                    M model = repository.read(Integer.parseInt(req.routeParam("id")));
                    if (model == null) {
                        next.run(new RecordNotFoundException());
                    }
                    req.body().transform(() -> model);
                }));
                pipeline.pipe(apiResponse(200));

            });

            controller.index(rootUrl, (pipeline) -> {
                pipeline.pipe(persistence.transactional(true, (req, res) -> {
                    req.body().transform(() -> repository.find().list());
                }));
                pipeline.pipe(apiResponse(200));
            });

            controller.update(rootUrl + "/{id}", (pipeline) -> {
                pipeline.pipe(BodyTransformers.fromJson(modelType))
                        .pipe(ValidationMiddleware.validateTransformedBody())
                        .pipe(persistence.transactional(false, (req, res) -> {
                            M model = repository.read(Integer.parseInt(req.routeParam("id")));
                            if (model == null) {
                                throw new RecordNotFoundException();
                            }
                            M jsonModel = req.body().asTransformed();
                            if (jsonModel.getId() != model.getId()) {
                                throw new ApiErrorException(400, "Mismatched entity ID");
                            }
                            repository.update(jsonModel);
                            req.body().transform(() -> true);
                        }))
                        .pipe(apiResponse(200));

            });

            controller.delete(rootUrl + "/{id}", (pipeline) -> {
                pipeline.pipe(persistence.transactional(false, (req, res, next) -> {
                    M model = repository.read(Integer.parseInt(req.routeParam("id")));
                    if (model == null) {
                        next.run(new RecordNotFoundException());
                    }
                    repository.delete(model);
                    req.body().transform(() -> true);
                }));
                pipeline.pipe(apiResponse(200));

            });

            controller.error(ApiErrorException.class, (e, req, res, next) -> {
                res.json(e.getStatusCode(), ApiResponse.error(e));
            });

            controller.error(ConstraintViolationException.class, (e, req, res, next) -> {
                Set<? extends ConstraintViolation<?>> violations = e.getConstraintViolations();
                ApiConstraintError error = violations == null || violations.isEmpty() ? new ApiConstraintError(e.getMessage()) : new ApiConstraintError();
                if (violations != null) {
                    for (ConstraintViolation<?> violation : violations) {
                        error.addConstraintViolation(violation);
                    }
                }
                res.json(error.getCode().httpStatus(), ApiResponse.error(error));
            });


            controller.error((e, req, res, next) -> {
                logger.error(e.getMessage(), e);
                res.json(500, ApiResponse.error(500, "Unknown Error"));
            });
        });
    }

    public static Middleware.BasicMiddleware<HttpRequest, HttpResponse> apiResponse(int status) {
        return (req, res) -> {
            res.json(status, ApiResponse.body(req.body().asTransformed()));
        };
    }
}

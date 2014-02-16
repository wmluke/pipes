package app.controllers;

import app.api.ApiResponse;
import app.exceptions.ApiErrorException;
import app.exceptions.RecordNotFoundException;
import app.models.User;
import net.bunselmeyer.evince.Evince;
import net.bunselmeyer.evince.http.HttpRequest;
import net.bunselmeyer.evince.http.HttpResponse;
import net.bunselmeyer.evince.persistence.Persistence;
import net.bunselmeyer.evince.persistence.Repository;
import net.bunselmeyer.hitch.middleware.Middleware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.bunselmeyer.hitch.middleware.BodyTransformers.json;

public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public static Evince app(Persistence persistence) {

        Repository<User> userRepository = persistence.build(User.class);

        Evince app = Evince.create();

        // CREATE
        app.post("/users", json(User.class));
        app.post("/users", persistence.transactional(false, (req, res) -> {
            User user = req.body().asTransformed();
            userRepository.create(user);
        }));
        app.post("/users", apiResponse(201));

        // INDEX
        app.get("/users", persistence.transactional(true, (req, res) -> {
            req.body().transform(() -> userRepository.find().list());
        }));
        app.get("/users", apiResponse(200));

        // READ
        app.get("/users/{id}", persistence.transactional(true, (req, res, next) -> {
            User user = userRepository.read(Integer.parseInt(req.routeParam("id")));
            if (user == null) {
                next.run(new RecordNotFoundException());
            }
            req.body().transform(() -> user);
        }));
        app.get("/users/{id}", apiResponse(200));

        // UPDATE
        app.put("/users/{id}", json(User.class));
        app.put("/users/{id}", persistence.transactional(false, (req, res) -> {
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
        }));
        app.put("/users/{id}", apiResponse(200));

        // DELETE
        app.delete("/users/{id}", persistence.transactional(false, (req, res, next) -> {
            User user = userRepository.read(Integer.parseInt(req.routeParam("id")));
            if (user == null) {
                next.run(new RecordNotFoundException());
            }
            userRepository.delete(user);
            req.body().transform(() -> true);
        }));
        app.delete("/users/{id}", apiResponse(200));

        // ERROR HANDLING

        app.use(ApiErrorException.class, (e, req, res, next) -> {
            res.json(e.getStatusCode(), ApiResponse.error(e));
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

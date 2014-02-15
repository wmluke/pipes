package app.controllers;

import app.models.User;
import net.bunselmeyer.evince.Evince;
import net.bunselmeyer.evince.persistence.Persistence;
import net.bunselmeyer.evince.persistence.Repository;

import java.util.List;

import static net.bunselmeyer.hitch.middleware.BodyTransformers.json;

public class UserController {

    public static Evince app(Persistence persistence) {

        Evince app = Evince.create();

        Repository<User> userRepository = persistence.build(User.class);

        app.post("/users", json(User.class));
        app.post("/users", persistence.transactional(false, (req, res) -> {
            User user = req.body().asTransformed();
            userRepository.create(user);
            res.json(201, user);
        }));

        app.get("/users", persistence.transactional(true, (req, res) -> {
            List<User> users = userRepository.find().list();
            res.json(200, users);
        }));

        app.get("/users/{id}", persistence.transactional(true, (req, res) -> {
            User user = userRepository.read(Integer.parseInt(req.routeParam("id")));
            if (user == null) {
                res.json(404);
                return;
            }
            res.json(200, user);
        }));

        app.put("/users/{id}", json(User.class));
        app.put("/users/{id}", persistence.transactional(false, (req, res) -> {
            User user = userRepository.read(Integer.parseInt(req.routeParam("id")));
            if (user == null) {
                res.json(404);
                return;
            }
            User jsonUser = req.body().asTransformed();
            if (jsonUser.getId() != user.getId()) {
                res.json(400);
                return;
            }
            userRepository.update(jsonUser);
            res.json(200);
        }));

        app.delete("/users/{id}", persistence.transactional(false, (req, res) -> {
            User user = userRepository.read(Integer.parseInt(req.routeParam("id")));
            userRepository.delete(user);
            res.json(200);
        }));

        return app;
    }
}

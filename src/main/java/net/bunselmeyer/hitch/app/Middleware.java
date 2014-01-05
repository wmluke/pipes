package net.bunselmeyer.hitch.app;

public interface Middleware {

    void run(Request req, Response resp);

}

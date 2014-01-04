package net.bunselmeyer.hitch.app;

import net.bunselmeyer.hitch.server.Request;
import net.bunselmeyer.hitch.server.Response;

public interface Middleware {

    void run(Request req, Response resp);

}

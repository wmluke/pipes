package net.bunselmeyer.hitch.server;

import net.bunselmeyer.hitch.app.App;

public interface HttpServer {

    HttpServer listen(int port) throws InterruptedException;

    public static HttpServer createHttpServer(App app) {
        return new HttpServerImpl(app);
    }
}

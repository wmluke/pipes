package net.bunselmeyer.middleware.server;

import net.bunselmeyer.middleware.pipes.Pipes;
import net.bunselmeyer.middleware.core.App;
import net.bunselmeyer.middleware.pipes.http.servlet.ServletApp;
import net.bunselmeyer.middleware.server.jetty.JettyHttpServer;
import net.bunselmeyer.middleware.server.netty.NettyHttpServer;

public interface HttpServer {

    HttpServer listen(int port) throws Exception;

    static HttpServer createJettyServer(ServletApp app) {
        return new JettyHttpServer(app);
    }

    static HttpServer createJettyServer(Pipes app) {
        return new JettyHttpServer(app.servlet());
    }

    static HttpServer createNettyHttpServer(App app) {
        return new NettyHttpServer(app);
    }
}

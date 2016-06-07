package net.bunselmeyer.middleware.server;

import net.bunselmeyer.middleware.core.RunnableApp;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import net.bunselmeyer.middleware.server.jetty.JettyHttpServer;
import net.bunselmeyer.middleware.server.netty.NettyHttpServer;

public interface HttpServer {

    HttpServer listen(int port) throws Exception;

    static HttpServer createJettyServer(RunnableApp<HttpRequest, HttpResponse> app) {
        return new JettyHttpServer(app);
    }

    static HttpServer createNettyHttpServer(RunnableApp<HttpRequest, HttpResponse> app) {
        return new NettyHttpServer(app);
    }
}

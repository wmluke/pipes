package net.bunselmeyer.middleware.server.jetty;

import net.bunselmeyer.middleware.core.App;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import net.bunselmeyer.middleware.server.HttpServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;

public class JettyHttpServer implements HttpServer {

    private final App<HttpRequest, HttpResponse, ?> app;

    public JettyHttpServer(App<HttpRequest, HttpResponse, ?> app) {
        this.app = app;
    }

    @Override
    public HttpServer listen(int port) throws Exception {

        SessionHandler sessionHandler = new SessionHandler(app.configuration(HashSessionManager.class));
        sessionHandler.setHandler(new MiddlewareHandler(app));

        ContextHandler context = new ContextHandler();
        context.setContextPath("/");
        context.setResourceBase(".");
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setHandler(sessionHandler);

        Server server = new Server(port);

        server.setSessionIdManager(new HashSessionIdManager());
        server.setHandler(context);

        server.start();
        server.join();

        return this;
    }
}

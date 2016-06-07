package net.bunselmeyer.middleware.server.jetty;

import net.bunselmeyer.middleware.core.RunnableApp;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import net.bunselmeyer.middleware.server.HttpServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.SessionHandler;

public class JettyHttpServer implements HttpServer {

    private final RunnableApp<HttpRequest, HttpResponse> app;

    public JettyHttpServer(RunnableApp<HttpRequest, HttpResponse> app) {
        this.app = app;
    }

    @Override
    public HttpServer listen(int port) throws Exception {

        SessionHandler sessionHandler = new SessionHandler(app.configuration(SessionManager.class));
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

package net.bunselmeyer.server.jetty;

import net.bunselmeyer.hitch.App;
import net.bunselmeyer.server.HttpServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JettyHttpServer implements HttpServer {

    private final App<HttpServletRequest, HttpServletResponse> app;

    public JettyHttpServer(App<HttpServletRequest, HttpServletResponse> app) {
        this.app = app;
    }

    @Override
    public HttpServer listen(int port) throws Exception {

        ContextHandler context = new ContextHandler();
        context.setContextPath("/");
        context.setResourceBase(".");
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setHandler(new MiddlewareHandler(app));

        Server server = new Server(port);
        server.setHandler(context);
        server.start();
        server.join();

        return this;
    }
}

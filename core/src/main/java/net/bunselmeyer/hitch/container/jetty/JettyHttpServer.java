package net.bunselmeyer.hitch.container.jetty;

import net.bunselmeyer.hitch.app.App;
import net.bunselmeyer.hitch.http.HttpServer;
import org.eclipse.jetty.server.Server;

public class JettyHttpServer implements HttpServer {

    private final App app;

    public JettyHttpServer(App app) {
        this.app = app;
    }


    @Override
    public HttpServer listen(int port) throws Exception {

        Server server = new Server(port);

        //HandlerList handlerList = new HandlerList();
        //handlerList.setHandlers();

        server.setHandler(new MiddlewareHandler(app));

        server.start();
        server.join();

        return this;
    }
}

package net.bunselmeyer.server;

import net.bunselmeyer.evince.Evince;
import net.bunselmeyer.hitch.App;
import net.bunselmeyer.hitch.Hitch;
import net.bunselmeyer.server.jetty.JettyHttpServer;
import net.bunselmeyer.server.netty.NettyHttpServer;

public interface HttpServer {

    HttpServer listen(int port) throws Exception;

    public static HttpServer createJettyServer(Hitch app) {
        return new JettyHttpServer(app);
    }

    public static HttpServer createJettyServer(Evince app) {
        return new JettyHttpServer(app.hitch());
    }

    public static HttpServer createNettyHttpServer(App app) {
        return new NettyHttpServer(app);
    }
}

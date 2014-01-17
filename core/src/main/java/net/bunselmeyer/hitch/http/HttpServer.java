package net.bunselmeyer.hitch.http;

import net.bunselmeyer.hitch.app.App;
import net.bunselmeyer.hitch.app.Evince;
import net.bunselmeyer.hitch.app.Hitch;
import net.bunselmeyer.hitch.container.jetty.JettyHttpServer;
import net.bunselmeyer.hitch.container.netty.NettyHttpServer;

import static net.bunselmeyer.hitch.middleware.AppMiddleware.evinceApp;

public interface HttpServer {

    HttpServer listen(int port) throws Exception;

    public static HttpServer createJettyServer(Hitch app) {
        return new JettyHttpServer(app);
    }

    public static HttpServer createJettyServer(Evince app) {
        Hitch hitch = Hitch.create();
        hitch.use(evinceApp(app));
        return new JettyHttpServer(hitch);
    }

    public static HttpServer createNettyHttpServer(App app) {
        return new NettyHttpServer(app);
    }
}

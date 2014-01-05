package net.bunselmeyer.hitch.app;

import net.bunselmeyer.hitch.netty.NettyHttpServer;

public interface HttpServer {

    HttpServer listen(int port) throws InterruptedException;

    public static HttpServer createNettyHttpServer(App app) {
        return new NettyHttpServer(app);
    }
}

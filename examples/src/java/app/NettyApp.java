package app;

import net.bunselmeyer.middleware.server.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyApp {
    private static final Logger logger = LoggerFactory.getLogger(JettyApp.class);

    public static void main(String[] args) throws Exception {
        int port = 8888;

        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        HttpServer.createNettyHttpServer(new ExampleApp()).listen(port);
    }
}

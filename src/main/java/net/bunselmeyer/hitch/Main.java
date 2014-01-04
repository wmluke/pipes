package net.bunselmeyer.hitch;

import net.bunselmeyer.hitch.app.App;
import net.bunselmeyer.hitch.server.HttpServer;

public class Main {

    public static void main(String[] args) throws Exception {

        App app = App.create();

        app.use((req, resp) -> {
            resp.charset("UTF-8");
            resp.type("text/html");

        });

        app.use((req, resp) -> {
            resp.send(200, "<h1>hello world!</h1>");
        });

        HttpServer.createHttpServer(app).listen(8888);

    }
}

import com.google.common.base.Joiner;
import net.bunselmeyer.hitch.app.App;
import net.bunselmeyer.hitch.http.HttpServer;
import net.bunselmeyer.hitch.middleware.Middleware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyApp {

    private static final Logger logger = LoggerFactory.getLogger(NettyApp.class);

    public static void main(String[] args) throws Exception {

        App app = App.create();

        app.use(Middleware.requestLogger(logger, false));

        app.use((req, res) -> {
            res.charset("UTF-8");
            res.type("text/html");
        });

        app.use((req, res) -> {
            if (req.uri().startsWith("/restricted")) {
                res.send(401, "Restricted Area");
            }
        });

        app.use((req, res) -> {
            res.cookie("foo", "bar", (cookie) -> {
                cookie.setPath("/");
                cookie.setHttpOnly(true);
            });
        });

        app.get("/", (req, res) -> {
            res.send(200, "<h1>hello world!</h1>");
        });

        app.get("/locations/{country}/{state}/{city}", (req, res) -> {
            String country = req.routeParam("country");
            String state = req.routeParam("state");
            String city = req.routeParam("city");
            res.send(200, "<h1>" + Joiner.on(", ").join(country, state, city) + "</h1>");
        });

        app.post("/", (req, res) -> {
            //String s = req.bodyPostParameter("aaa");
            String s = req.body().asText();
            res.send(200, "<h1>bye bye world!</h1>\n<p>" + s + "</p>");
        });

        HttpServer.createNettyHttpServer(app).listen(8989);

    }
}

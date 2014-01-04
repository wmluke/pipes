# Hitch

> Middleware for Java 1.8

A naive port of [Connect](http://www.senchalabs.org/connect) to Java 1.8 to try out Java's fancy new Lambda support!  Under the hood, Hitch uses [netty](http://netty.io).

## Example

```java
public class Main {

    public static void main(String[] args) throws Exception {

        App app = App.create();

        app.use((req, resp) -> {
            resp.charset("UTF-8");
            resp.type("text/html");
        });

        app.use((req, resp) -> {
            resp.cookie("foo", "bar", (cookie) -> {
                cookie.setPath("/");
                cookie.setHttpOnly(true);
            });
            resp.send(200, "<h1>hello world!</h1>");
        });

        HttpServer.createHttpServer(app).listen(8888);

    }
}
```

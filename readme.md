# Pipes

> Middleware for Java 1.8

[![Build Status](https://travis-ci.org/wmluke/pipes.png?branch=master)](https://travis-ci.org/wmluke/pipes)

Pipes is a middleware framework for Java 1.8.

Pipes is heavily inspired by [Connect](http://www.senchalabs.org/connect) and [Express](http://expressjs.com) for node.
Specifically, these node middleware frameworks allow developers to create robust web applications by composing simple and lightweight middleware.  
Hopefully, Pipes can leverage Java's fancy new Lambda support to bring this same spirit to Java. Under the hood, Pipes uses [Jetty](http://www.eclipse.org/jetty).

## Install

Add to `pipes-core` to your project's pom.xml file:

```xml
<repositories>
    <repository>
        <id>sonatype-nexus-snapshots</id>
        <name>Sonatype Nexus Snapshots</name>
        <url>https://oss.sonatype.org/content/groups/public</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>net.bunselmeyer</groupId>
        <artifactId>pipes-core</artifactId>
        <version>0.1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## Hello World Example

Create a main method and setup either a `Pipes` app within the main method...

```java
public class App {

    public static void main(String[] args) throws Exception {

        Pipes app = Pipes.create();

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
        
        app.get("/stream")
            .pipe((req1, res1) -> {
                return Stream.of("one", "two", "three");
            })
            .pipe((memo, req, res) -> {
                return memo.map(String::length);
            });
            
        app.get("/locations/{country}/{state}/{city}").pipe((req, res) -> {
            String country = req.routeParam("country");
            String state = req.routeParam("state");
            String city = req.routeParam("city");
            res.send(200, "<h1>" + Joiner.on(", ").join(country, state, city) + "</h1>");
        });            

        HttpServer.createJettyServer(app).listen(8888);

    }
}
```

Run it...

```bash
$ mvn exec:java -Dexec.mainClass="App"
```

## Run the Example App

The [example app](https://github.com/wmluke/pipes/blob/master/examples/src/java/app/JettyApp.java) illustrates more of Pipes's features beyond a simple hello world app.

Run `make install` then `make run` from the command line.

```bash
$ make install  # build & test
$ make run      # run the example app
```

See the [Makefile](https://github.com/wmluke/Pipes/blob/master/Makefile) for other commands.

## License
MIT

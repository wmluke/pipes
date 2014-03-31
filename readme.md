# Hitch/Evince

> Middleware for Java 1.8

[![Build Status](https://travis-ci.org/wmluke/hitch.png?branch=master)](https://travis-ci.org/wmluke/hitch)

Hitch & Evince are a middleware frameworks for Java 1.8.

Hitch & Evince are heavily inspired by [Connect](http://www.senchalabs.org/connect) and [Express](http://expressjs.com) for node.  Specifically, these node middleware frameworks allow developers to create robust web applications by composing simple and lightweight middleware.  Hopefully, Hitch & Evince can leverage Java's fancy new Lambda support to bring this same spirit to Java. Under the hood, Hitch & Evince use [Jetty](http://www.eclipse.org/jetty).

## Install

Add to `hitch-core` to your project's pom.xml file:

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
        <artifactId>hitch-core</artifactId>
        <version>0.1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## Hello World Example

Create a main method and setup either a `Hitch` or `Evince` app within the main method...

```java
public class App {

    public static void main(String[] args) throws Exception {

        Evince app = Evince.create();

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

        HttpServer.createJettyServer(app).listen(8888);

    }
}
```

Run it...

```bash
$ mvn exec:java -Dexec.mainClass="App"
```

## Run the Example App

The [example app](https://github.com/wmluke/hitch/blob/master/examples/src/java/app/JettyApp.java) illustrates more of Hitch's features beyond a simple hello world app.

1) Install [Java 8](https://jdk8.java.net) and create a `JAVA8_HOME` environment variable referencing the JDK 8 home.

```bash
export JAVA8_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home
```

2) Run `make install` then `make run` from the command line.

```bash
$ make install  # build & test
$ make run      # run the example app
```

See the [Makefile](https://github.com/wmluke/hitch/blob/master/Makefile) for other commands.

## License
MIT

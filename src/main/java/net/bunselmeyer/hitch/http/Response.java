package net.bunselmeyer.hitch.http;

import io.netty.handler.codec.http.Cookie;

import java.nio.charset.Charset;
import java.util.function.Consumer;

public interface Response {

    boolean isCommitted();

    Response status(int status);

    Integer status();

    Response header(String name, String value);

    String header(String name);

    Response cookie(String name, Cookie value);

    Response cookie(String name, String value, Consumer<Cookie> cookieOptions);

    Response clearCookie(String name);

    Response redirect(String url);

    Response charset(String charset);

    Response charset(Charset charset);

    Charset charset();

    Response type(String type);

    String type();

    Response send(int status);

    Response send(int status, String body);

    Response send(String body);

    Response json(int status);

    Response json(int status, String body);

    Response json(String body);


    public static javax.servlet.http.Cookie servletCookie(Cookie nettyCookie) {
        javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(nettyCookie.getName(), nettyCookie.getValue());
        c.setHttpOnly(nettyCookie.isHttpOnly());
        c.setComment(nettyCookie.getComment());
        if (nettyCookie.getDomain() != null) {
            c.setDomain(nettyCookie.getDomain());
        }
        c.setPath(nettyCookie.getPath());
        c.setMaxAge((int) nettyCookie.getMaxAge());
        c.setSecure(nettyCookie.isSecure());
        c.setVersion(nettyCookie.getVersion());
        return c;
    }
}

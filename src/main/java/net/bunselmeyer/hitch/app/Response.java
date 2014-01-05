package net.bunselmeyer.hitch.app;

import io.netty.handler.codec.http.Cookie;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public interface Response {

    Response status(int status);

    Integer status();

    Response header(String name, String value);

    String header(String name);

    Response cookie(String name, Cookie value);

    Response cookie(String name, String value, Options<Cookie> cookieOptions);

    Cookie cookie(String name);

    Response clearCookie(String name);

    Response redirect(int status, String url);

    Response redirect(String url) throws IOException;

    Response charset(String charset);

    Response charset(Charset charset);

    Charset charset();

    Response type(String type);

    String type();

    Response send(int status) throws IOException;

    Response send(int status, String body) throws IOException;

    Response send(String body) throws IOException;

    Response json(int status) throws IOException;

    Response json(int status, String body) throws IOException;

    Response json(String body);

    String body();

    Map<String, String> headers();

    Map<String, Cookie> cookies();


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
